/*
 * Copyright (C) 2015 Brett Wooldridge
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

package com.zaxxer.hikari.metrics;

import static com.zaxxer.hikari.util.ClockSource.currentTime;
import static com.zaxxer.hikari.util.ClockSource.plusMillis;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Brett Wooldridge
 */
public abstract class PoolStats
{
   private final AtomicLong reloadAt;
   private final long timeoutMs;

   // 总连接,包括空闲的连接和使用中的连接(idleConnections+activeConnections)
   protected volatile int totalConnections;
   // 空闲连接数
   protected volatile int idleConnections;
   // 活跃连接数
   protected volatile int activeConnections;
   // 正在等待连接的线程数量,排查性能问题时这个指标是一个重要的参考指标,如果等待连接的线程在相当一段时间内数较多,可以考虑扩大数据库连接池的maxSize
   protected volatile int pendingThreads;
   // 最大连接数,统计指标,统计到目前为止连接的最大数量
   protected volatile int maxConnections;
   // 最小连接数,统计指标,统计到目前为上连接的最小数量
   protected volatile int minConnections;

   public PoolStats(final long timeoutMs)
   {
      this.timeoutMs = timeoutMs;
      this.reloadAt = new AtomicLong();
   }

   public int getTotalConnections()
   {
      if (shouldLoad()) {
         update();
      }

      return totalConnections;
   }

   public int getIdleConnections()
   {
      if (shouldLoad()) {
         update();
      }

      return idleConnections;
   }

   public int getActiveConnections()
   {
      if (shouldLoad()) {
         update();
      }

      return activeConnections;
   }

   public int getPendingThreads()
   {
      if (shouldLoad()) {
         update();
      }

      return pendingThreads;
   }

   public int getMaxConnections() {
      if (shouldLoad()) {
         update();
      }

      return maxConnections;
   }

   public int getMinConnections() {
      if (shouldLoad()) {
         update();
      }

      return minConnections;
   }

   protected abstract void update();

   private boolean shouldLoad()
   {
      for (; ; ) {
          final long now = currentTime();
          final long reloadTime = reloadAt.get();
          if (reloadTime > now) {
              return false;
          }
          else if (reloadAt.compareAndSet(reloadTime, plusMillis(now, timeoutMs))) {
              return true;
          }
      }
  }
}
