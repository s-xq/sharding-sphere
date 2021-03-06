/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.proxy.transport.mysql.packet.command.admin.initdb;

import com.google.common.base.Optional;
import io.shardingsphere.core.constant.ShardingConstant;
import io.shardingsphere.proxy.transport.mysql.constant.ServerErrorCode;
import io.shardingsphere.proxy.transport.mysql.packet.MySQLPacketPayload;
import io.shardingsphere.proxy.transport.mysql.packet.command.CommandPacket;
import io.shardingsphere.proxy.transport.mysql.packet.command.CommandPacketType;
import io.shardingsphere.proxy.transport.mysql.packet.command.CommandResponsePackets;
import io.shardingsphere.proxy.transport.mysql.packet.generic.ErrPacket;
import io.shardingsphere.proxy.transport.mysql.packet.generic.OKPacket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * COM_INIT_DB command packet.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-init-db.html#packet-COM_INIT_DB">COM_INIT_DB</a>
 *
 * @author zhangliang
 */
@Slf4j
public final class ComInitDbPacket implements CommandPacket {
    
    @Getter
    private final int sequenceId;
    
    private final String schemaName;
    
    public ComInitDbPacket(final int sequenceId, final MySQLPacketPayload payload) {
        this.sequenceId = sequenceId;
        schemaName = payload.readStringEOF();
    }
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeInt1(CommandPacketType.COM_INIT_DB.getValue());
        payload.writeStringEOF(schemaName);
    }
    
    @Override
    public Optional<CommandResponsePackets> execute() {
        log.debug("Schema name received for Sharding-Proxy: {}", schemaName);
        return Optional.of(ShardingConstant.LOGIC_SCHEMA_NAME.equalsIgnoreCase(schemaName)
                ? new CommandResponsePackets(new OKPacket(getSequenceId() + 1)) : new CommandResponsePackets(new ErrPacket(getSequenceId() + 1, ServerErrorCode.ER_BAD_DB_ERROR, schemaName)));
    }
}
