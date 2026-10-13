package dev.tobiazsh.imguib3d.client.natives.drawlist;

import dev.tobiazsh.imguib3d.client.natives.NativeLoader;

import java.nio.ByteBuffer;

public final class ImDrawListExt {
    static {
        NativeLoader.load("drawlist_accessor");
    }

    /**
     * Native method, which returns the vertex buffer of the provided draw list pointer.
     * The returned draw list is supposed to be modifiable.
     *
     * <p>
     *     You can get the pointer of the ImDrawList by getting {@link imgui.ImDrawList#ptr}
     *
     * <p>
     *     <strong>Justification:</strong> This method fills the gap that the newest version of <code>imgui-java</code>
     *     has opened; multiple methods of the new font rendering system are yet to be implemented, making features like
     *     rotated text essentially impossible: You can't currently draw your own glyphs at the specified location. This
     *     method is a Band-Aid until the proper implementation is published.
     *
     * <p>
     *     <strong>⚠️️ Safety warning ⚠️ (READ!)</strong>
     *     <p>
     *         It is generally not recommended that you use this method. If possible, please consider using a solution
     *         natively supported by ImGui. If you do decide to use it, please make sure that you safely implement it.
     *         You can find a list of stuff you'd want to keep in mind for a generally safe implementation below.
     *     <p>
     *         <i>How do I safely use this method?</i>
     *         <ul>
     *             <li>
     *                 The buffer is only valid until the next addition of vertices. You cannot reuse the same vertex
     *                 buffer after you have added another vertex. It will most likely point to invalid memory, leading
     *                 to a segfault. Always add vertices first, then modify.
     *             </li>
     *             <li>
     *                 Each vertex is <strong>exactly 20 bytes long:</strong>
     *                 <ul>
     *                     <li><code>pos.x</code> / float, 4 bytes, offset 0</li>
     *                     <li><code>pos.y</code> / float, 4 bytes, offset 4</li>
     *                     <li><code>uv.x</code>  / float, 4 bytes, offset 8</li>
     *                     <li><code>uv.y</code>  / float, 4 bytes, offset 12</li>
     *                     <li><code>col</code>   / float, 4 bytes, offset 16</li>
     *                 </ul>
     *             </li>

     *             <li>
     *                 ImGui's DrawList endianness is dictated by the systems default. Java's ByteBuffer is always
     *                 big-endian-ordered. You must reorder the ByteBuffer to match the system's default before using.
     *             </li>
     *             <li>
     *                 No resizing: You can only modify existing vertices. To add vertices, you'd need to first add it
     *                 using one of {@link imgui.ImDrawList}'s add methods.
     *             </li>
     *         </ul>
     *
     * @param drawListPtr The pointer to the ImDrawList you want the vertex buffer of.
     * @return A direct ByteBuffer, which contains the ImDrawList's data.
     */
    public static native ByteBuffer getVtxBuffer(long drawListPtr); /*
        ImDrawList* drawList = reinterpret_cast<ImDrawList*>(drawListPtr);

        if (!drawList || drawList->VtxBuffer.Size == 0)
            return nullptr;

        size_t sizeBytes = drawList->VtxBuffer.Size * sizeof(ImDrawVert);

        return env->NewDirectByteBuffer(drawList->VtxBuffer.Data, sizeBytes);
    */

    /**
     * Native method, which returns the size of the given ImDrawList.
     *
     * @param drawListPtr The pointer of the ImDrawList, which you can find at {@link imgui.ImDrawList#ptr}.
     * @return The size of the ImDrawList's data in an integer.
     */
    public static native int getVtxBufferSize(long drawListPtr); /*
        ImDrawList* drawList = reinterpret_cast<ImDrawList*>(drawListPtr);
        return drawList ? drawList->VtxBuffer.Size : 0;
    */
}
