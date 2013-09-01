package com.rendl.auth.elasticsearch

import javax.servlet.{ServletContextEvent, ServletContextListener}
import com.rendl.auth.service.elasticsearch.ElasticSearchClient

class ElasticSearchConnectionListener extends ServletContextListener {
  def contextInitialized(p1: ServletContextEvent) {
    ElasticSearchClient.init()
  }

  def contextDestroyed(p1: ServletContextEvent) {
    ElasticSearchClient.close()
  }
}