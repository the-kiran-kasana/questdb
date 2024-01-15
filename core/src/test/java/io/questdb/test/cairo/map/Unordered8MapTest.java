/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.test.cairo.map;

import io.questdb.cairo.CairoException;
import io.questdb.cairo.ColumnType;
import io.questdb.cairo.SingleColumnType;
import io.questdb.cairo.map.MapKey;
import io.questdb.cairo.map.MapValue;
import io.questdb.cairo.map.Unordered8Map;
import io.questdb.cairo.sql.Record;
import io.questdb.cairo.sql.RecordCursor;
import io.questdb.std.Chars;
import io.questdb.test.AbstractCairoTest;
import io.questdb.test.tools.TestUtils;
import org.junit.Assert;
import org.junit.Test;

public class Unordered8MapTest extends AbstractCairoTest {

    @Test
    public void testSingleZeroKey() {
        try (Unordered8Map map = new Unordered8Map(new SingleColumnType(ColumnType.LONG), new SingleColumnType(ColumnType.LONG), 16, 0.8, 24)) {
            MapKey key = map.withKey();
            key.putLong(0);
            MapValue value = key.createValue();
            Assert.assertTrue(value.isNew());
            value.putLong(0, 42);

            try (RecordCursor cursor = map.getCursor()) {
                final Record record = cursor.getRecord();
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(0, record.getLong(1));
                Assert.assertEquals(42, record.getLong(0));

                // Validate that we get the same sequence after toTop.
                cursor.toTop();
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(0, record.getLong(1));
                Assert.assertEquals(42, record.getLong(0));
            }
        }
    }

    @Test
    public void testTwoKeysIncludingZero() {
        try (Unordered8Map map = new Unordered8Map(new SingleColumnType(ColumnType.LONG), new SingleColumnType(ColumnType.LONG), 16, 0.8, 24)) {
            MapKey key = map.withKey();
            key.putLong(0);
            MapValue value = key.createValue();
            Assert.assertTrue(value.isNew());
            value.putLong(0, 0);

            key = map.withKey();
            key.putLong(1);
            value = key.createValue();
            Assert.assertTrue(value.isNew());
            value.putLong(0, 1);

            try (RecordCursor cursor = map.getCursor()) {
                final Record record = cursor.getRecord();
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(1, record.getLong(1));
                Assert.assertEquals(1, record.getLong(0));
                // Zero is always last when iterating.
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(0, record.getLong(1));
                Assert.assertEquals(0, record.getLong(0));

                // Validate that we get the same sequence after toTop.
                cursor.toTop();
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(1, record.getLong(1));
                Assert.assertEquals(1, record.getLong(0));
                Assert.assertTrue(cursor.hasNext());
                Assert.assertEquals(0, record.getLong(1));
                Assert.assertEquals(0, record.getLong(0));
            }
        }
    }

    @Test
    public void testUnsupportedKeyBinary() throws Exception {
        TestUtils.assertMemoryLeak(() -> {
            try (Unordered8Map ignore = new Unordered8Map(new SingleColumnType(ColumnType.BINARY), new SingleColumnType(ColumnType.LONG), 64, 0.5, 1)) {
                Assert.fail();
            } catch (CairoException e) {
                Assert.assertTrue(Chars.contains(e.getMessage(), "unexpected key size"));
            }
        });
    }

    @Test
    public void testUnsupportedKeyLong128() throws Exception {
        TestUtils.assertMemoryLeak(() -> {
            try (Unordered8Map ignore = new Unordered8Map(new SingleColumnType(ColumnType.LONG128), new SingleColumnType(ColumnType.LONG), 64, 0.5, 1)) {
                Assert.fail();
            } catch (CairoException e) {
                Assert.assertTrue(Chars.contains(e.getMessage(), "unexpected key size"));
            }
        });
    }

    @Test
    public void testUnsupportedKeyLong256() throws Exception {
        TestUtils.assertMemoryLeak(() -> {
            try (Unordered8Map ignore = new Unordered8Map(new SingleColumnType(ColumnType.LONG256), new SingleColumnType(ColumnType.LONG), 64, 0.5, 1)) {
                Assert.fail();
            } catch (CairoException e) {
                Assert.assertTrue(Chars.contains(e.getMessage(), "unexpected key size"));
            }
        });
    }

    @Test
    public void testUnsupportedKeyString() throws Exception {
        TestUtils.assertMemoryLeak(() -> {
            try (Unordered8Map ignore = new Unordered8Map(new SingleColumnType(ColumnType.STRING), new SingleColumnType(ColumnType.LONG), 64, 0.5, 1)) {
                Assert.fail();
            } catch (CairoException e) {
                Assert.assertTrue(Chars.contains(e.getMessage(), "unexpected key size"));
            }
        });
    }
}