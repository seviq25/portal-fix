package com.seviq.portalfix;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;


public class PortalFixAttachments {
    public static final AttachmentType<Long> PORTAL_PERSISTENCE_EXPIRY = AttachmentRegistry.createPersistent(
            PortalFix.id("portal_persistence_expiry"),
            Codec.LONG
    );
}
