#include <jni.h>
#include "imgui/imgui.h"

extern "C"
JNIEXPORT jobject JNICALL Java_dev_tobiazsh_imguib3d_client_natives_drawlist_ImDrawListExt_getVtxBuffer(
    JNIEnv* env,
    jclass clazz,
    jlong drawListPtr
) {
    ImDrawList* drawList = reinterpret_cast<ImDrawList*>(drawListPtr);

    if (!drawList || drawList->VtxBuffer.Size == 0)
        return nullptr;

    size_t sizeBytes = drawList->VtxBuffer.Size * sizeof(ImDrawVert);

    return env->NewDirectByteBuffer(drawList->VtxBuffer.Data, sizeBytes);
}

extern "C"
JNIEXPORT jint JNICALL Java_dev_tobiazsh_imguib3d_client_natives_drawlist_ImDrawListExt_getVtxBufferSize(
    JNIEnv *env,
    jclass clazz,
    jlong drawListPtr
) {
    ImDrawList* drawList = reinterpret_cast<ImDrawList*>(drawListPtr);
    return drawList ? drawList->VtxBuffer.Size : 0;
}
