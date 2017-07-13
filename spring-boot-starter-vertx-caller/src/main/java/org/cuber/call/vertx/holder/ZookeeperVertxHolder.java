package org.cuber.call.vertx.holder;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

import java.util.concurrent.CompletableFuture;

/**
 * Created by cuber on 2017/7/12.
 */
public class ZookeeperVertxHolder extends AbstractVertxHolder {
    private String zookeeperHosts;

    private String rootPath;

    private JsonObject retryPolicy = new JsonObject()
            .put("initialSleepTime", 3000)
            .put("maxTimes", 3);


    public ZookeeperVertxHolder(String zookeeperHosts, String rootPath, JsonObject retryPolicy) {
        this.zookeeperHosts = zookeeperHosts;
        this.rootPath = rootPath;
        this.retryPolicy = retryPolicy;
    }

    public ZookeeperVertxHolder() {

    }

    public ZookeeperVertxHolder(String zookeeperHosts, String rootPath) {
        this.zookeeperHosts = zookeeperHosts;
        this.rootPath = rootPath;
    }

    synchronized Vertx productOneVertx(String key) throws Exception{
        JsonObject zkConfig = new JsonObject();
        zkConfig.put("zookeeperHosts", zookeeperHosts);
        zkConfig.put("rootPath", this.rootPath+"." + key);
        zkConfig.put("retry", this.retryPolicy);
        ClusterManager mgr = new ZookeeperClusterManager(zkConfig);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        CompletableFuture<Vertx> future = new CompletableFuture<>();
        Vertx.clusteredVertx(options, rs ->{
            if(rs.succeeded()){
                future.complete(rs.result());
            }else{
                future.completeExceptionally(rs.cause());
            }
        });
        return future.get();
    }

    public String getZookeeperHosts() {
        return zookeeperHosts;
    }

    public void setZookeeperHosts(String zookeeperHosts) {
        this.zookeeperHosts = zookeeperHosts;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public JsonObject getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(JsonObject retryPolicy) {
        this.retryPolicy = retryPolicy;
    }
}
