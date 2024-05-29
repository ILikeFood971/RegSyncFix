# RegSyncFix

This is a simple Fabric mod that bypasses errors during registry syncs in Fabric API (example below). It can be used on the server side
to disable syncs entirely, or you can install it on your client and the client will proceed despite the error.

![Example of the error RegSyncFix fixes](example_error.png)

Tested on 1.20.4 but will probably work on just about any version of Minecraft with Fabric API.

## WARNING

The registry sync is there for a reason. If you are using this mod you may encounter unexpected behavior if the mods are
not the same as the server. The intent of this mod is for cases where you know everything should work fine but the
registry is preventing you from joining.

