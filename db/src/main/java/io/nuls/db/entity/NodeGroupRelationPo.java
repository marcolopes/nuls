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
package io.nuls.db.entity;

/**
 * @author Niels
 * @date 2017/11/20
 */
public class NodeGroupRelationPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column node_group_relation.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column node_group_relation.node_id
     *
     * @mbg.generated
     */
    private Integer nodeId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column node_group_relation.group_id
     *
     * @mbg.generated
     */
    private Integer groupId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column node_group_relation.id
     *
     * @return the value of node_group_relation.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column node_group_relation.id
     *
     * @param id the value for node_group_relation.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column node_group_relation.node_id
     *
     * @return the value of node_group_relation.node_id
     *
     * @mbg.generated
     */
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column node_group_relation.node_id
     *
     * @param nodeId the value for node_group_relation.node_id
     *
     * @mbg.generated
     */
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column node_group_relation.group_id
     *
     * @return the value of node_group_relation.group_id
     *
     * @mbg.generated
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column node_group_relation.group_id
     *
     * @param groupId the value for node_group_relation.group_id
     *
     * @mbg.generated
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
