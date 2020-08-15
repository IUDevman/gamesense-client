package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

public class RenderEvent extends GameSenseEvent {

        private final Tessellator tessellator;
        private final Vec3d renderPos;
        private final float partialTicks;

        public RenderEvent(Tessellator tessellator, Vec3d renderPos, float ticks) {
            super();
            this.tessellator = tessellator;
            this.renderPos = renderPos;
            partialTicks = ticks;
        }

        public Tessellator getTessellator() {
            return tessellator;
        }

        public BufferBuilder getBuffer() {
            return tessellator.getBuffer();
        }

        public Vec3d getRenderPos() {
            return renderPos;
        }

        public void setTranslation(Vec3d translation) {
            getBuffer().setTranslation(-translation.x, -translation.y, -translation.z);
        }

        public void resetTranslation() {
            setTranslation(renderPos);
        }

        public float getPartialTicks(){
            return partialTicks;
        }
}
