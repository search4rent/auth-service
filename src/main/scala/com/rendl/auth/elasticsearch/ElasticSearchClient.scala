package com.rendl.auth.service.elasticsearch

import javax.servlet.{ServletContextEvent, ServletContextListener}
import org.elasticsearch.client.Client
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.node.Node
import org.elasticsearch.node.{NodeBuilder, Node}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 7/22/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Trait to access the elasticsearch client.
 */


/**
 * Holds the elasticsearch client.
 */
object ElasticSearchClient {
  var client: Client = _
  private var node: Node = _
  lazy val settingDev = ImmutableSettings.settingsBuilder();
  settingDev.put("node.name", "Lasher")
  settingDev.put("discovery.zen.ping.multicast.enabled", false)
  settingDev.put("discovery.zen.ping.unicast.hosts", "localhost:9300")

  def init() {
    node = NodeBuilder.nodeBuilder().client(true).clusterName("elasticsearch").settings(settingDev).node()
    client = node.client()
  }

  def close() {
    client.close()
    node.close()
  }

  /**
   * Chars that must be escaped in a query.
   */
  val SPECIAL_SEQUENCES = """+ - && || ! ( ) { } [ ] ^ " ~ * ? :""".split(' ')
  val ESCAPED_SEQUENCES = Map(SPECIAL_SEQUENCES.map(seq => (seq, seq.map("\\" + _).foldLeft("")(_ + _))): _*)
}
