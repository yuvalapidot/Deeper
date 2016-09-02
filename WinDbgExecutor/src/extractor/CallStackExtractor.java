package extractor;

import memory.model.Call;
import memory.model.CallStack;

import java.io.BufferedReader;
import java.io.IOException;

public class CallStackExtractor extends AbstractExtractor <CallStack> {

    private static final String[] indicators = {"Child-SP", "RetAddr", "Args to Child", "Call Site"};
    private static final int CHILD_SP = 0;
    private static final int RET_ADDR = 1;
    private static final int ARGS_BASE = 3;
    private static final int CALL_SITE = 8;

    @Override
    public CallStack extract(BufferedReader reader) throws IOException {
        log.debug("Extracting call stack from output");
        CallStack callStack = new CallStack();
        String line;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
            boolean indicatorLine = true;
            for (String indicator : indicators) {
                indicatorLine = indicatorLine && line.contains(indicator);
                if (!indicatorLine) {
                    break;
                }
            }
            if (indicatorLine) {
                while (!(line = readLine(reader)).isEmpty()) {
                    callStack.prependCall(lineToCall(line));
                }
            }
        }
        return callStack;
    }

    private Call lineToCall(String line) {
        Call call = new Call();
        String[] chunks = line.split(" ");
        call.setChildSp(chunks[CHILD_SP]);
        call.setReturnAddress(chunks[RET_ADDR]);
        for (int i = 0; i < Call.ARGS_COUNT; i++) {
            call.setArg(chunks[ARGS_BASE + i], i);
        }
        call.setCallSite(chunks[CALL_SITE]);
        return call;
    }
}
