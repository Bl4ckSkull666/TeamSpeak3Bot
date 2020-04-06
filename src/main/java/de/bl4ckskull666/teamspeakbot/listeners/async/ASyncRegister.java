package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.*;

public class ASyncRegister implements TS3Listener {
    @Override
    public void onTextMessage(TextMessageEvent textMessageEvent) {
        try {
            ASyncCommands.Do(textMessageEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientJoin(ClientJoinEvent clientJoinEvent) {
        try {
            ASyncClientJoin.Do(clientJoinEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
        ASyncClientLeave.Do(clientLeaveEvent);
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
        try {
            ASyncClientMove.Do(clientMovedEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
