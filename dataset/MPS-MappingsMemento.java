/*
 * Copyright 2003-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.generator.impl.cache;

import jetbrains.mps.util.Pair;
import jetbrains.mps.util.io.ModelInputStream;
import jetbrains.mps.util.io.ModelOutputStream;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Persistence-friendly snapshot of mapping labels.
 * Evgeny Gryaznov, Sep 30, 2010
 */
public class MappingsMemento {

  /* mapping,input -> output */
  private final Map<String, Map<SNodeId, Object>> myMappingNameAndInputNodeToOutputNodeMap = new HashMap<>();

  /* input -> output */
  private final Map<SNodeId, Object> myCopiedOutputNodeForInputNode = new HashMap<>();

  private final List<Pair<String, SNodeId>> myConditionalRoots = new ArrayList<>();


  // add functions

  public void addOutputNodeByInputNodeAndMappingName(SNodeId inputNode, String mappingName, Object value) {
    if (mappingName == null) return;
    Map<SNodeId, Object> currentMapping = myMappingNameAndInputNodeToOutputNodeMap.get(mappingName);
    if (currentMapping == null) {
      myMappingNameAndInputNodeToOutputNodeMap.put(mappingName, new HashMap<>());
      currentMapping = myMappingNameAndInputNodeToOutputNodeMap.get(mappingName);
    }
    if (value instanceof SNodeId) {
      currentMapping.put(inputNode, value);
    } else if (value instanceof SNode) {
      currentMapping.put(inputNode, ((SNode) value).getNodeId());
    } else if (value instanceof Collection) {
      @SuppressWarnings("unchecked")
      Collection<SNode> n0 = (Collection<SNode>) value;
      List<SNodeId> v = new ArrayList<>(n0.size());
      for (SNode n : n0) {
        v.add(n.getNodeId());
      }
      currentMapping.put(inputNode, v);
    }
  }

  public void addOutputNodeByInputNode(SNodeId inputNode, SNodeId outputNode, boolean isUnique) {
    myCopiedOutputNodeForInputNode.put(inputNode, isUnique ? outputNode : Collections.singletonList(outputNode));
  }

  public void addNewOutputNode(String mappingLabel, SNodeId outputNode) {
    myConditionalRoots.add(new Pair<>(mappingLabel, outputNode));
  }

  // getters

  public Map<String, Map<SNodeId, Object>> getMappingNameAndInputNodeToOutputNodeMap() {
    return myMappingNameAndInputNodeToOutputNodeMap;
  }

  public Map<SNodeId, Object> getCopiedOutputNodeForInputNode() {
    return myCopiedOutputNodeForInputNode;
  }

  public Collection<SNodeId> getNewOutputNodes(String mappingLabel) {
    return myConditionalRoots.stream().filter(p -> p.o1.equals(mappingLabel)).map(p -> p.o2).collect(Collectors.toList());
  }

  // serialization

  public void save(ModelOutputStream os) throws IOException {

    /* mapping,input -> output */
    os.writeInt(myMappingNameAndInputNodeToOutputNodeMap.size());
    for (Entry<String, Map<SNodeId, Object>> e : myMappingNameAndInputNodeToOutputNodeMap.entrySet()) {
      os.writeString(e.getKey());
      Map<SNodeId, Object> innerMap = e.getValue();
      os.writeInt(innerMap.size());
      for (Entry<SNodeId, Object> v : innerMap.entrySet()) {
        os.writeNodeId(v.getKey());
        Object value = v.getValue();
        if (value instanceof SNodeId) {
          os.writeInt(1);
          os.writeNodeId((SNodeId) value);
        } else if (value instanceof List) {
          os.writeInt(((List) value).size());
          for (SNodeId id : (List<SNodeId>) value) {
            os.writeNodeId(id);
          }
        }
      }
    }

    /* input -> output */
    os.writeInt(myCopiedOutputNodeForInputNode.size());
    for (Entry<SNodeId, Object> e : myCopiedOutputNodeForInputNode.entrySet()) {
      os.writeNodeId(e.getKey());
      Object val = e.getValue();
      if (val instanceof SNodeId) {
        os.writeNodeId((SNodeId) val);
        os.writeBoolean(true);
      } else {
        os.writeNodeId(((List<SNodeId>) val).get(0));
        os.writeBoolean(false);
      }
    }

    /* check */
    os.writeByte('!');
  }

  public static MappingsMemento load(ModelInputStream is) throws IOException {
    MappingsMemento mappingsMemento = new MappingsMemento();

    /* mapping,input -> output */
    for (int size = is.readInt(); size > 0; size--) {
      String label = is.readString();
      int mapSize = is.readInt();
      Map<SNodeId, Object> innerMap = new HashMap<>(mapSize);
      for (; mapSize > 0; mapSize--) {
        SNodeId key = is.readNodeId();
        int valSize = is.readInt();
        if (valSize == 1) {
          innerMap.put(key, is.readNodeId());
        } else {
          List<SNodeId> list = new ArrayList<>(valSize);
          for (; valSize > 0; valSize--) {
            list.add(is.readNodeId());
          }
          innerMap.put(key, list);
        }
      }
      mappingsMemento.myMappingNameAndInputNodeToOutputNodeMap.put(label, innerMap);
    }

    /* input -> output */
    for (int size = is.readInt(); size > 0; size--) {
      SNodeId inputNode = is.readNodeId();
      SNodeId outputNode = is.readNodeId();
      boolean isUnique = is.readBoolean();
      mappingsMemento.addOutputNodeByInputNode(inputNode, outputNode, isUnique);
    }

    /* check */
    if (is.readByte() != '!') {
      throw new IOException("inconsistent cache");
    }
    return mappingsMemento;
  }


}
