/*
 * Copyright (c) 2021
 *
 * This file is part of ReplayStudio.
 *
 * ReplayStudio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ReplayStudio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReplayStudio.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.replaymod.replaystudio.protocol.packets;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.replaymod.replaystudio.protocol.Packet;
import com.replaymod.replaystudio.protocol.PacketType;
import com.replaymod.replaystudio.protocol.PacketTypeRegistry;
import com.replaymod.replaystudio.protocol.registry.DimensionType;

import java.io.IOException;

public class PacketRespawn {
    public byte gameMode;
    public byte prevGameMode; // 1.16+
    public DimensionType dimensionType;
    public String dimension;
    public long seed; // 1.15+
    public int difficulty; // pre 1.14
    public boolean debugWorld; // 1.16+
    public boolean flatWorld; // 1.16+
    public boolean keepPlayerAttributes; // 1.16+

    public static PacketRespawn read(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            PacketRespawn respawn = new PacketRespawn();
            respawn.read(packet, in);
            return respawn;
        }
    }

    public void read(Packet packet, Packet.Reader in) throws IOException {
        if (packet.atLeast(ProtocolVersion.v1_16)) {
            if (packet.atLeast(ProtocolVersion.v1_16_2)) {
                this.dimensionType = new DimensionType(in.readNBT());
            } else {
                this.dimensionType = new DimensionType(in.readString());
            }
            this.dimension = in.readString();
        } else {
            this.dimension = String.valueOf(in.readInt());
        }
        if (packet.atLeast(ProtocolVersion.v1_15)) {
            this.seed = in.readLong();
        }
        if (packet.olderThan(ProtocolVersion.v1_14)) {
            this.difficulty = in.readByte();
        }
        this.gameMode = in.readByte();
        if (packet.atLeast(ProtocolVersion.v1_16)) {
            this.prevGameMode = in.readByte();
        }
        if (packet.atLeast(ProtocolVersion.v1_16)) {
            this.debugWorld = in.readBoolean();
            this.flatWorld = in.readBoolean();
            this.keepPlayerAttributes = in.readBoolean();
        } else {
            this.dimensionType = new DimensionType(in.readString());
        }
    }

    public Packet write(PacketTypeRegistry registry) throws IOException {
        Packet packet = new Packet(registry, PacketType.Respawn);
        try (Packet.Writer out = packet.overwrite()) {
            write(packet, out);
        }
        return packet;
    }

    public void write(Packet packet, Packet.Writer out) throws IOException {
        if (packet.atLeast(ProtocolVersion.v1_16)) {
            if (packet.atLeast(ProtocolVersion.v1_16_2)) {
                out.writeNBT(this.dimensionType.getTag());
            } else {
                out.writeString(this.dimensionType.getName());
            }
            out.writeString(this.dimension);
        } else if (packet.atLeast(ProtocolVersion.v1_9_1)) {
            out.writeInt(Integer.parseInt(this.dimension));
        } else {
            out.writeByte(Integer.parseInt(this.dimension));
        }
        if (packet.atLeast(ProtocolVersion.v1_15)) {
            out.writeLong(this.seed);
        }
        if (packet.olderThan(ProtocolVersion.v1_14)) {
            out.writeByte(this.difficulty);
        }
        out.writeByte(this.gameMode);
        if (packet.atLeast(ProtocolVersion.v1_16)) {
            out.writeByte(this.prevGameMode);
            out.writeBoolean(this.debugWorld);
            out.writeBoolean(this.flatWorld);
            out.writeBoolean(this.keepPlayerAttributes);
        } else {
            out.writeString(this.dimensionType.getName());
        }
    }
}
