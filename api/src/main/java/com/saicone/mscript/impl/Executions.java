package com.saicone.mscript.impl;

import com.saicone.mscript.Execution;
import com.saicone.mscript.impl.execution.ActionBarExecution;
import com.saicone.mscript.impl.execution.DelayExecution;
import com.saicone.mscript.impl.execution.MessageExecution;
import com.saicone.mscript.impl.execution.ResultExecution;
import com.saicone.mscript.impl.execution.TitleExecution;
import com.saicone.mscript.io.SectionReader;

public final class Executions {

    public static final SectionReader<? extends Execution> ACTIONBAR = ActionBarExecution.READER;
    public static final SectionReader<? extends Execution> DELAY = DelayExecution.READER;
    public static final SectionReader<? extends Execution> MESSAGE = MessageExecution.READER;
    public static final SectionReader<? extends Execution> RESULT = ResultExecution.READER;
    public static final SectionReader<? extends Execution> TITLE = TitleExecution.READER;

    Executions() {
    }
}
