package admicloud.storm.wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;


public class TotalViewTopology {
  private static Logger LOG = LoggerFactory.getLogger(TotalViewTopology.class);

  public static class RandomSentenceSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    Random _rand;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
      _collector = collector;
      _rand = new Random();
    }

    @Override
    public void nextTuple() {
      Utils.sleep(100);
      String[] bookHistory = new String[]{ "{'Math': 9, 'Chemistry': 8, 'Hello Book': 5}", "{'Math': 9, 'Chemistry': 8, 'Hello Book': 5}","{'Math': 9, 'Chemistry': 8, 'Hello Book': 5}","{'Math': 9, 'Chemistry': 8, 'Hello Book': 5}" };
      String book = bookHistory[_rand.nextInt(bookHistory.length)];
      _collector.emit(new Values(book));
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("bookHistory"));
    }
  }

  public static class ExtractBooksValues extends BaseBasicBolt {
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("book"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
      return null;
    }

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
      String bookHistory = tuple.getStringByField("bookHistory");
      JSONObject object = new JSONObject(bookHistory);
      int math = (int)object.get("Math");
      int chemistry = (int)object.get("Chemistry");
      int helloBook = (int)object.get("Hello Book");
      basicOutputCollector.emit(new Values(math));
      basicOutputCollector.emit(new Values(chemistry));
      basicOutputCollector.emit(new Values(helloBook));
    }
  }

  public static class TotalCount extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
      String book = tuple.getString(0);
      Integer count = counts.get(book);
      if (count == null)
        count = 0;
      count++;
      counts.put(book, count);
      LOG.info("Total count of viewed books: " + count);
      collector.emit(new Values(book, count));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("book", "count"));
    }
  }

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("spout", new RandomSentenceSpout(), 5);

    builder.setBolt("split", new ExtractBooksValues(), 8).shuffleGrouping("spout");
    builder.setBolt("count", new TotalCount(), 12).fieldsGrouping("split", new Fields("book"));

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("book-count", conf, builder.createTopology());

      Thread.sleep(10000);

      cluster.shutdown();
    }
  }
}
