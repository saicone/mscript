package com.saicone.mscript.test.io;

import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.test.condition.TestPermissionCondition;

public class TestScriptReader extends ScriptReader {

    public TestScriptReader() {

        this.conditions.put(TestPermissionCondition.READER);
    }
}
