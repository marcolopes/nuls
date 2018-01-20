/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.rpc.resources.impl;

import io.nuls.rpc.entity.RpcResult;
import io.nuls.rpc.resources.BlockResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Niels
 * @date 2017/9/30
 *
 */
@Path("/block")
public class BlockResourceImpl implements BlockResource {
    @GET
    @Path("/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult loadBlock(@PathParam("hash") String hash) {
        return null;
    }
    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getBlockCount() {
        return null;
    }
    @GET
    @Path("/bestheight")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getBestBlockHeiht() {
        return null;
    }
    @GET
    @Path("/besthash")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getBestBlockHash() {
        return null;
    }
    @GET
    @Path("/height/{height}/hash")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getHashByHeight(@PathParam("height")Integer height) {
        return null;
    }
    @GET
    @Path("/height/{height}/header")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getHeaderByHeight(@PathParam("height")Integer height) {
        return null;
    }
    @GET
    @Path("/{hash}/header")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getHeader(@PathParam("hash")String hash) {
        return null;
    }
    @GET
    @Path("/height/{height}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RpcResult getBlock(@PathParam("height")Integer height) {
        return null;
    }
}
