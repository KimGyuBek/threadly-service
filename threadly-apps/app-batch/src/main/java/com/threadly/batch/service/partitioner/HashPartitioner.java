package com.threadly.batch.service.partitioner;

import java.util.Map;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class HashPartitioner implements Partitioner {


  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> map = new LinkedMap<>();
    for (int shard = 0; shard < gridSize; shard++) {
      ExecutionContext executionContext = new ExecutionContext();
      executionContext.put("shard", shard);
      executionContext.put("gridSize", gridSize);
      map.put("part-" + shard, executionContext);
    }
    return map;
  }
}
