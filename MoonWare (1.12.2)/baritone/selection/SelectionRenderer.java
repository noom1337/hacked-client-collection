package baritone.selection;

import baritone.Baritone;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.selection.ISelection;
import baritone.utils.IRenderer;
import net.minecraft.util.math.AxisAlignedBB;

public class SelectionRenderer implements IRenderer, AbstractGameEventListener {

    public static final double SELECTION_BOX_EXPANSION = .005D;

    private final SelectionManager manager;

    SelectionRenderer(Baritone baritone, SelectionManager manager) {
        this.manager = manager;
        baritone.getGameEventHandler().registerEventListener(this);
    }

    public static void renderSelections(ISelection[] selections) {
        float opacity = IRenderer.settings.selectionOpacity.value;
        boolean ignoreDepth = IRenderer.settings.renderSelectionIgnoreDepth.value;
        float lineWidth = IRenderer.settings.selectionLineWidth.value;

        if (!IRenderer.settings.renderSelection.value) {
            return;
        }

        IRenderer.startLines(IRenderer.settings.colorSelection.value, opacity, lineWidth, ignoreDepth);

        for (ISelection selection : selections) {
            IRenderer.drawAABB(selection.aabb(), SELECTION_BOX_EXPANSION);
        }

        if (IRenderer.settings.renderSelectionCorners.value) {
            IRenderer.glColor(IRenderer.settings.colorSelectionPos1.value, opacity);

            for (ISelection selection : selections) {
                IRenderer.drawAABB(new AxisAlignedBB(selection.pos1(), selection.pos1().add(1, 1, 1)));
            }

            IRenderer.glColor(IRenderer.settings.colorSelectionPos2.value, opacity);

            for (ISelection selection : selections) {
                IRenderer.drawAABB(new AxisAlignedBB(selection.pos2(), selection.pos2().add(1, 1, 1)));
            }
        }

        IRenderer.endLines(ignoreDepth);
    }

    @Override
    public void onRenderPass(RenderEvent event) {
        renderSelections(manager.getSelections());
    }
}
