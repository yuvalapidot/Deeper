package extractor;

import javafx.util.Pair;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Process;
import model.memory.Sequence;
import model.memory.Thread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sequence.ISequenceFinder;
import sequence.PrefixSpanSequenceFinder;

import java.util.*;

public class SequenceExtractor extends AbstractFeatureExtractor<DumpInstance> {

    private int minimumSupport;
    private int maximumSupport;
    private int minimumSequenceLength;
    private int maximumSequenceLength;
    private int batchSize;

    private final Logger log = LogManager.getLogger(SequenceExtractor.class);

    public SequenceExtractor(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength, int batchSize) {
        this.minimumSupport = minimumSupport;
        this.maximumSupport = maximumSupport;
        this.minimumSequenceLength = minimumSequenceLength;
        this.maximumSequenceLength = maximumSequenceLength;
        this.batchSize = batchSize;
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }

    @Override
    public void extractToDataBase() {
        getAllSequences(true);
    }

    @Override
    public void extract(DataTable table) {
//        List<Map<Sequence, List<Pair<DumpInstance, Integer>>>> mapList = getAllSequences(false);
//        for (Map<Sequence, List<Pair<DumpInstance, Integer>>> map : mapList) {
//            for (Sequence sequence : map.keySet()) {
//                FeatureKey<Sequence, Integer> featureKey = new FeatureKey<>(sequence, 0);
//                for (Pair<DumpInstance, Integer> dumpInfo : map.get(sequence)) {
//                    FeatureValue<Integer> featureValue = new FeatureValue<>(dumpInfo.getValue());
//                    table.put(dumpInfo.getKey(), featureKey, featureValue);
//                }
//            }
//        }
        getAllSequences(table);
    }

    private List<Map<Sequence, List<Pair<DumpInstance, Integer>>>> getAllSequences(boolean saveToDataBase) {
        List<Map<Sequence, List<Pair<DumpInstance, Integer>>>> mapList = new ArrayList<>(10);
        int i = 0;
        for (Set<DumpInstance> dumpBatch : getDumpsBatches()) {
            ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
            log.info("Starting on " + i + " batch. Time = " + new Date().getTime());
            mapList.add(getAllDumpsSequences(dumpBatch, finder, saveToDataBase));
            log.info("End on " + i + " batch. Time = " + new Date().getTime() + ". Number of sequences = " + mapList.get(i).size());
            i++;
        }
        return mapList;
    }

    private void getAllSequences(DataTable table) {
        int i = 0;
        for (Set<DumpInstance> dumpBatch : getDumpsBatches()) {
            ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
            log.info("Starting on batch " + i + ".");
            long startTime = new Date().getTime();
            Map<Sequence, List<Pair<DumpInstance, Integer>>> batchSequences = getAllDumpsSequences(dumpBatch, finder, false);
            for (Sequence sequence : batchSequences.keySet()) {
                Sequence featureKey = sequence;
                for (Pair<DumpInstance, Integer> dumpInfo : batchSequences.get(sequence)) {
                    Integer featureValue = dumpInfo.getValue();
                    table.put(dumpInfo.getKey(), featureKey, featureValue);
                }
            }
            log.info("End on " + i + " batch. Time = " + (new Date().getTime() - startTime) + ". Number of sequences = " + batchSequences.size());
            System.gc();
            i++;
        }
    }

    private List<Set<DumpInstance>> getDumpsBatches() {
        List<Set<DumpInstance>> batches = new ArrayList<>();
        int counter = 0;
        Set<DumpInstance> batch = new LinkedHashSet<>();
        for (DumpInstance instance : instances) {
                batch.add(instance);
                counter++;
                if (counter % batchSize == 0) {
                    batches.add(batch);
                    batch = new LinkedHashSet<>();
                }
        }
        if (!batch.isEmpty()) {
            batches.add(batch);
        }
        return batches;
    }

    private Map<Sequence, List<Pair<DumpInstance, Integer>>> getAllDumpsSequences(Set<DumpInstance> dumps, ISequenceFinder finder, boolean saveToDataBase) {
        List<DumpInstance> dumpList = new ArrayList<>();
        List<List<Call>> sequences = new ArrayList<>();
        prepareDumpsAndSequences(dumps, dumpList, sequences);
        return finder.generateSubSequences(dumpList, sequences, saveToDataBase);
    }

    private void getAllDumpsSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, Set<DumpInstance> dumps, ISequenceFinder finder, boolean saveToDataBase) {
        List<DumpInstance> dumpList = new ArrayList<>();
        List<List<Call>> sequences = new ArrayList<>();
        prepareDumpsAndSequences(dumps, dumpList, sequences);
        finder.generateSubSequences(map, dumpList, sequences, saveToDataBase);
    }

    private void prepareDumpsAndSequences(Set<DumpInstance> dumps, List<DumpInstance> dumpList, List<List<Call>> sequences) {
        for (DumpInstance dumpInstance : dumps) {
            for (Process process : dumpInstance.getInstance().getProcesses()) {
                for (Thread thread : process.getThreads()) {
                    dumpList.add(dumpInstance);
                    sequences.add(thread.getCallStack().getCallList());
                }
            }
        }
    }

}
