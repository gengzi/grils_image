package fun.gengzi.swing;

import com.intellij.ide.DeleteProvider;
import com.intellij.ide.util.DeleteHandler;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLayeredPane;
import com.intellij.ui.components.Magnificator;
import com.intellij.util.LazyInitializer.NotNullValue;
import com.intellij.util.ui.JBUI;
import kotlin.jvm.internal.Intrinsics;
import lombok.SneakyThrows;
import org.intellij.images.ImagesBundle;
import org.intellij.images.editor.ImageDocument;
import org.intellij.images.editor.ImageDocument.ScaledImageProvider;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActions;
import org.intellij.images.options.*;
import org.intellij.images.thumbnail.actionSystem.ThumbnailViewActions;
import org.intellij.images.ui.ImageComponent;
import org.intellij.images.ui.ImageComponentDecorator;
import org.intellij.images.vfs.IfsUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * <h1>默认图片ui</h1>
 * <p>
 * 可以参考
 *
 * @author gengzi
 * @date 2022年2月5日16:28:20
 * @see org.intellij.images.editor.impl.ImageEditorUI
 */
public class DefaultImageEditorUI extends JPanel implements DataProvider, ImageComponentDecorator, Disposable {
    @NonNls
    private static final String IMAGE_PANEL = "image";
    @NonNls
    private static final String ERROR_PANEL = "error";
    @NonNls
    private static final String ZOOM_FACTOR_PROP = "ImageEditor.zoomFactor";

    private VirtualFile virtualFile;
    private final DeleteProvider deleteProvider;
//  private final CopyPasteSupport copyPasteSupport;

    // 图片缩放
    private final ImageZoomModel zoomModel = new ImageZoomModelImpl();
    private final ImageWheelAdapter wheelAdapter = new ImageWheelAdapter();
    private final ChangeListener changeListener = new DocumentChangeListener();
    // 图片组件
    private final ImageComponent imageComponent = new ImageComponent();
    private final JPanel contentPanel;
    private JLabel infoLabel = null;

    private final JScrollPane myScrollPane;


    /**
     * 构造方法
     *
     * @param virtualFile
     */
    public DefaultImageEditorUI(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
        // 添加监听器
        imageComponent.addPropertyChangeListener(ZOOM_FACTOR_PROP, e -> imageComponent.setZoomFactor(getZoomModel().getZoomFactor()));
        Options options = OptionsManager.getInstance().getOptions();
        EditorOptions editorOptions = options.getEditorOptions();
        options.addPropertyChangeListener(new OptionsChangeListener(), this);

//    copyPasteSupport = editor != null ? new CopyPasteDelegator(editor.getProject(), this) : null;
        deleteProvider = new DeleteHandler.DefaultDeleteProvider();

        ImageDocument document = imageComponent.getDocument();
        document.addChangeListener(changeListener);

        // Set options
        TransparencyChessboardOptions chessboardOptions = editorOptions.getTransparencyChessboardOptions();
        GridOptions gridOptions = editorOptions.getGridOptions();
        imageComponent.setTransparencyChessboardCellSize(chessboardOptions.getCellSize());
        imageComponent.setTransparencyChessboardWhiteColor(chessboardOptions.getWhiteColor());
        imageComponent.setTransparencyChessboardBlankColor(chessboardOptions.getBlackColor());
        imageComponent.setGridLineZoomFactor(gridOptions.getLineZoomFactor());
        imageComponent.setGridLineSpan(gridOptions.getLineSpan());
        imageComponent.setGridLineColor(gridOptions.getLineColor());

        // Create layout
        ImageContainerPane view = new ImageContainerPane(imageComponent);
        view.addMouseListener(new EditorMouseAdapter());
        view.addMouseListener(new FocusRequester());

        myScrollPane = ScrollPaneFactory.createScrollPane(view, true);
        myScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        myScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Zoom by wheel listener
        myScrollPane.addMouseWheelListener(wheelAdapter);

        // Construct UI
        setLayout(new BorderLayout());


        // toolbar is disabled in embedded mode
        JComponent toolbarPanel = null;
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction(ImageEditorActions.GROUP_TOOLBAR);
        ActionToolbar actionToolbar = actionManager.createActionToolbar(
                ImageEditorActions.ACTION_PLACE, actionGroup, true
        );


        actionToolbar.setTargetComponent(this);

        toolbarPanel = actionToolbar.getComponent();
        toolbarPanel.addMouseListener(new FocusRequester());


        JLabel errorLabel = new JLabel(
                ImagesBundle.message("error.broken.image.file.format"),
                Messages.getErrorIcon(), SwingConstants.CENTER
        );

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorLabel, BorderLayout.CENTER);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(myScrollPane, IMAGE_PANEL);
        contentPanel.add(errorPanel, ERROR_PANEL);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolbarPanel, BorderLayout.WEST);
        infoLabel = new JLabel((String) null, SwingConstants.RIGHT);
        infoLabel.setBorder(JBUI.Borders.emptyRight(2));
        topPanel.add(infoLabel, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        myScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateZoomFactor();
            }
        });

        updateInfo();
    }


    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }


    /**
     * 从vsf 展示对应的图片资源
     *
     * @param file {@link VirtualFile} 虚拟文件
     */
    @SneakyThrows
    public void showImage(VirtualFile file) {
        this.virtualFile = file;
        // 获取 ImageComponent
        ImageComponent imageComponent = this.getImageComponent();
        final BufferedImage image = IfsUtil.getImage(this.virtualFile, imageComponent);
        // 设置图片资源
        imageComponent.getDocument().setValue(image);
        // 设置图片格式
        imageComponent.getDocument().setFormat(this.virtualFile.getFileType().getName());
    }

    /**
     * 从vsf 展示对应的图片资源
     *
     * @param image {@link BufferedImage} 虚拟文件
     */
    @SneakyThrows
    public void showImage(BufferedImage image,String imgType) {
        // 设置图片资源
        imageComponent.getDocument().setValue(image);
        // 设置图片格式
        imageComponent.getDocument().setFormat(imgType);
    }


    private void updateInfo() {
        ImageDocument document = imageComponent.getDocument();
        BufferedImage image = document.getValue();
        if (image != null) {
            ColorModel colorModel = image.getColorModel();
            String format = document.getFormat();
            format = StringUtil.toUpperCase(format);
            VirtualFile file = this.virtualFile;
            infoLabel.setText(
                    ImagesBundle.message("image.info",
                            image.getWidth(), image.getHeight(), format,
                            colorModel.getPixelSize(), file != null ? StringUtil.formatFileSize(file.getLength()) : ""));
        } else {
            infoLabel.setText(null);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    JComponent getContentComponent() {
        return contentPanel;
    }

    ImageComponent getImageComponent() {
        return imageComponent;
    }

    @Override
    public void dispose() {
        imageComponent.removeMouseWheelListener(wheelAdapter);
        imageComponent.getDocument().removeChangeListener(changeListener);

        removeAll();
    }

    @Override
    public void setTransparencyChessboardVisible(boolean visible) {
        imageComponent.setTransparencyChessboardVisible(visible);
        repaint();
    }

    @Override
    public boolean isTransparencyChessboardVisible() {
        return imageComponent.isTransparencyChessboardVisible();
    }

    @Override
    public boolean isEnabledForActionPlace(String place) {
        // Disable for thumbnails action
        return !ThumbnailViewActions.ACTION_PLACE.equals(place);
    }


    @Override
    public void setGridVisible(boolean visible) {
        imageComponent.setGridVisible(visible);
        repaint();
    }

    @Override
    public boolean isGridVisible() {
        return imageComponent.isGridVisible();
    }

    @Override
    public ImageZoomModel getZoomModel() {
        return zoomModel;
    }

    public void setImageProvider(ScaledImageProvider imageProvider, String format) {
        ImageDocument document = imageComponent.getDocument();
        BufferedImage previousImage = document.getValue();
        document.setValue(imageProvider);
        if (imageProvider == null) return;
        document.setFormat(format);

        if (previousImage == null || !zoomModel.isZoomLevelChanged()) {
            Options options = OptionsManager.getInstance().getOptions();
            ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

            if (!(zoomOptions.isSmartZooming() && updateZoomFactor())) {
                zoomModel.setZoomFactor(1.0);
            }
        }
    }

    private boolean updateZoomFactor() {
        Options options = OptionsManager.getInstance().getOptions();
        ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

        if (zoomOptions.isSmartZooming() && !zoomModel.isZoomLevelChanged()) {
            Double smartZoomFactor = getSmartZoomFactor(zoomOptions);
            if (smartZoomFactor != null) {
                zoomModel.setZoomFactor(smartZoomFactor);
                return true;
            }
        }
        return false;
    }

    private final class ImageContainerPane extends JBLayeredPane {
        private final ImageComponent imageComponent;

        ImageContainerPane(final ImageComponent imageComponent) {
            this.imageComponent = imageComponent;
            add(imageComponent);

            putClientProperty(Magnificator.CLIENT_PROPERTY_KEY, new Magnificator() {
                @Override
                public Point magnify(double scale, Point at) {
                    Point locationBefore = imageComponent.getLocation();
                    ImageZoomModel model = getZoomModel();
                    double factor = model.getZoomFactor();
                    model.setZoomFactor(scale * factor);
                    return new Point(((int) ((at.x - Math.max(scale > 1.0 ? locationBefore.x : 0, 0)) * scale)),
                            ((int) ((at.y - Math.max(scale > 1.0 ? locationBefore.y : 0, 0)) * scale)));
                }
            });
        }

        private void centerComponents() {
            Rectangle bounds = getBounds();
            Point point = imageComponent.getLocation();
            // in embedded mode images should be left-side aligned
            point.x = (bounds.width - imageComponent.getWidth()) / 2;
            point.y = (bounds.height - imageComponent.getHeight()) / 2;
            imageComponent.setLocation(point);
        }

        @Override
        public void invalidate() {
            centerComponents();
            super.invalidate();
        }

        @Override
        public Dimension getPreferredSize() {
            return imageComponent.getSize();
        }
    }

    private final class ImageWheelAdapter implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            Options options = OptionsManager.getInstance().getOptions();
            EditorOptions editorOptions = options.getEditorOptions();
            ZoomOptions zoomOptions = editorOptions.getZoomOptions();
            if (zoomOptions.isWheelZooming() && e.isControlDown()) {
                int rotation = e.getWheelRotation();
                double oldZoomFactor = zoomModel.getZoomFactor();
                Point oldPosition = myScrollPane.getViewport().getViewPosition();

                if (rotation > 0) {
                    zoomModel.zoomOut();
                } else if (rotation < 0) {
                    zoomModel.zoomIn();
                }

                // reset view, otherwise view size is not obtained correctly sometimes
                Component view = myScrollPane.getViewport().getView();
                myScrollPane.setViewport(null);
                myScrollPane.setViewportView(view);

                if (oldZoomFactor > 0 && rotation != 0) {
                    Point mousePoint = e.getPoint();
                    double zoomChange = zoomModel.getZoomFactor() / oldZoomFactor;
                    Point newPosition = new Point((int) Math.max(0, (oldPosition.getX() + mousePoint.getX()) * zoomChange - mousePoint.getX()),
                            (int) Math.max(0, (oldPosition.getY() + mousePoint.getY()) * zoomChange - mousePoint.getY()));
                    myScrollPane.getViewport().setViewPosition(newPosition);
                }

                e.consume();
            }
        }
    }

    /**
     * 图片缩放实现
     */
    private class ImageZoomModelImpl implements ImageZoomModel {
        private boolean myZoomLevelChanged;
        private final NotNullValue<Double> IMAGE_MAX_ZOOM_FACTOR = new NotNullValue<Double>() {
            @NotNull
            @Override
            public Double initialize() {
                return Double.MAX_VALUE;
            }
        };
        private double zoomFactor = 0.0d;

        @Override
        public double getZoomFactor() {
            return zoomFactor;
        }

        @Override
        public void setZoomFactor(double zoomFactor) {
            double oldZoomFactor = getZoomFactor();

            if (Double.compare(oldZoomFactor, zoomFactor) == 0) return;
            this.zoomFactor = zoomFactor;

            // Change current size
            updateImageComponentSize();

            revalidate();
            repaint();
            myZoomLevelChanged = false;

            imageComponent.firePropertyChange(ZOOM_FACTOR_PROP, oldZoomFactor, zoomFactor);
        }

        private double getMaximumZoomFactor() {
            double factor = IMAGE_MAX_ZOOM_FACTOR.get();
            return Math.min(factor, MACRO_ZOOM_LIMIT);
        }

        private double getMinimumZoomFactor() {
            Rectangle bounds = imageComponent.getDocument().getBounds();
            double factor = bounds != null ? 1.0d / bounds.getWidth() : 0.0d;
            return Math.max(factor, MICRO_ZOOM_LIMIT);
        }

        @Override
        public void fitZoomToWindow() {
            Options options = OptionsManager.getInstance().getOptions();
            ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

            Double smartZoomFactor = getSmartZoomFactor(zoomOptions);
            if (smartZoomFactor != null) {
                zoomModel.setZoomFactor(smartZoomFactor);
            } else {
                zoomModel.setZoomFactor(1.0d);
            }
            myZoomLevelChanged = false;
        }

        @Override
        public void zoomOut() {
            setZoomFactor(getNextZoomOut());
            myZoomLevelChanged = true;
        }

        @Override
        public void zoomIn() {
            setZoomFactor(getNextZoomIn());
            myZoomLevelChanged = true;
        }

        private double getNextZoomOut() {
            double factor = getZoomFactor();
            if (factor > 1.0d) {
                // Macro
                factor /= MACRO_ZOOM_RATIO;
                factor = Math.max(factor, 1.0d);
            } else {
                // Micro
                factor /= MICRO_ZOOM_RATIO;
            }
            return Math.max(factor, getMinimumZoomFactor());
        }

        private double getNextZoomIn() {
            double factor = getZoomFactor();
            if (factor >= 1.0d) {
                // Macro
                factor *= MACRO_ZOOM_RATIO;
            } else {
                // Micro
                factor *= MICRO_ZOOM_RATIO;
                factor = Math.min(factor, 1.0d);
            }
            return Math.min(factor, getMaximumZoomFactor());
        }

        @Override
        public boolean canZoomOut() {
            // Ignore small differences caused by floating-point arithmetic.
            return getZoomFactor() - 1.0e-14 > getMinimumZoomFactor();
        }

        @Override
        public boolean canZoomIn() {
            return getZoomFactor() < getMaximumZoomFactor();
        }

        @Override
        public void setZoomLevelChanged(boolean value) {
            myZoomLevelChanged = value;
        }

        @Override
        public boolean isZoomLevelChanged() {
            return myZoomLevelChanged;
        }
    }

    @Nullable
    private Double getSmartZoomFactor(@NotNull ZoomOptions zoomOptions) {
        Rectangle bounds = imageComponent.getDocument().getBounds();
        if (bounds == null) return null;
        if (bounds.getWidth() == 0 || bounds.getHeight() == 0) return null;
        int width = bounds.width;
        int height = bounds.height;

        Dimension preferredMinimumSize = zoomOptions.getPrefferedSize();
        if (width < preferredMinimumSize.width &&
                height < preferredMinimumSize.height) {
            double factor = (preferredMinimumSize.getWidth() / (double) width +
                    preferredMinimumSize.getHeight() / (double) height) / 2.0d;
            return Math.ceil(factor);
        }

        Dimension canvasSize = myScrollPane.getViewport().getExtentSize();
        canvasSize.height -= ImageComponent.IMAGE_INSETS * 2;
        canvasSize.width -= ImageComponent.IMAGE_INSETS * 2;
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return null;

        if (canvasSize.width < width ||
                canvasSize.height < height) {
            return Math.min((double) canvasSize.height / height,
                    (double) canvasSize.width / width);
        }

        return 1.0d;
    }

    private void updateImageComponentSize() {
        Rectangle bounds = imageComponent.getDocument().getBounds();
        if (bounds != null) {
            final double zoom = getZoomModel().getZoomFactor();
            imageComponent.setCanvasSize((int) Math.ceil(bounds.width * zoom), (int) Math.ceil(bounds.height * zoom));
        }
    }

    private class DocumentChangeListener implements ChangeListener {
        @Override
        public void stateChanged(@NotNull ChangeEvent e) {
            updateImageComponentSize();

            ImageDocument document = imageComponent.getDocument();
            BufferedImage value = document.getValue();

            CardLayout layout = (CardLayout) contentPanel.getLayout();
            layout.show(contentPanel, value != null ? IMAGE_PANEL : ERROR_PANEL);

            updateInfo();

            revalidate();
            repaint();
        }
    }

    private class FocusRequester extends MouseAdapter {
        @Override
        public void mousePressed(@NotNull MouseEvent e) {
            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(DefaultImageEditorUI.this, true));
        }
    }

    private static final class EditorMouseAdapter extends PopupHandler {
        @Override
        public void invokePopup(Component comp, int x, int y) {
            // Single right click
            ActionManager actionManager = ActionManager.getInstance();
            ActionGroup actionGroup = (ActionGroup) actionManager.getAction(ImageEditorActions.GROUP_POPUP);
            ActionPopupMenu menu = actionManager.createActionPopupMenu(ImageEditorActions.ACTION_PLACE, actionGroup);
            JPopupMenu popupMenu = menu.getComponent();
            popupMenu.pack();
            popupMenu.show(comp, x, y);
        }
    }


    @Override
    @Nullable
    public Object getData(@NotNull String dataId) {
        Intrinsics.checkNotNullParameter(dataId, "dataId");
        return ImageComponentDecorator.DATA_KEY.is(dataId) ? this : null;
    }


    private static class ImageTransferable implements Transferable {
        private final BufferedImage myImage;

        ImageTransferable(@NotNull BufferedImage image) {
            myImage = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
            return DataFlavor.imageFlavor.equals(dataFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
            if (!DataFlavor.imageFlavor.equals(dataFlavor)) {
                throw new UnsupportedFlavorException(dataFlavor);
            }
            return myImage;
        }
    }

    private class OptionsChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Options options = (Options) evt.getSource();
            EditorOptions editorOptions = options.getEditorOptions();
            TransparencyChessboardOptions chessboardOptions = editorOptions.getTransparencyChessboardOptions();
            GridOptions gridOptions = editorOptions.getGridOptions();

            imageComponent.setTransparencyChessboardCellSize(chessboardOptions.getCellSize());
            imageComponent.setTransparencyChessboardWhiteColor(chessboardOptions.getWhiteColor());
            imageComponent.setTransparencyChessboardBlankColor(chessboardOptions.getBlackColor());
            imageComponent.setGridLineZoomFactor(gridOptions.getLineZoomFactor());
            imageComponent.setGridLineSpan(gridOptions.getLineSpan());
            imageComponent.setGridLineColor(gridOptions.getLineColor());
        }
    }

}
