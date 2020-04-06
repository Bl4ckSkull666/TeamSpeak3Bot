package de.bl4ckskull666.teamspeakbot.listeners.sync;

import com.github.theholywaffle.teamspeak3.api.event.*;

public class SyncRegister implements TS3Listener {
    @Override
    public void onTextMessage(TextMessageEvent textMessageEvent) {
        SyncCommands.Do(textMessageEvent);
    }

    @Override
    public void onClientJoin(ClientJoinEvent clientJoinEvent) {
        SyncClientJoin.Do(clientJoinEvent);
    }

    @Override
    public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
        SyncClientLeave.Do(clientLeaveEvent);
    }

    @Override
    public void onServerEdit(ServerEditedEvent serverEditedEvent) {

    }

    @Override
    public void onChannelEdit(ChannelEditedEvent channelEditedEvent) {

    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {

    }

    @Override
    public void onClientMoved(ClientMovedEvent clientMovedEvent) {
        SyncClientMove.Do(clientMovedEvent);
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent channelCreateEvent) {

    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {

    }

    @Override
    public void onChannelMoved(ChannelMovedEvent channelMovedEvent) {

    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {

    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {

    }
}
