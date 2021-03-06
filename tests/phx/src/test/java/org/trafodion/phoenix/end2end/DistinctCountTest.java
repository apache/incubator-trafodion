/*******************************************************************************
 * Copyright (c) 2013, Salesforce.com, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     Neither the name of Salesforce.com nor the names of its contributors may 
 *     be used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/**********************************
 *
 * Later modifications to test Trafodion instead of Phoenix were granted to ASF.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*************************************/

package test.java.org.trafodion.phoenix.end2end;

import static org.junit.Assert.*;
import org.junit.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DistinctCountTest extends BaseTest {

    @BeforeClass
    public static void doTestSuiteSetup() throws Exception {
        /* List all of the object names being used in this entire class.
         * The objects are dropped with errors ignored, so it is OK if the
         * object does not exist for a particular test.
         */
        objDropList = new ArrayList<String>(
            Arrays.asList("table " + ATABLE_NAME));
        doBaseTestSuiteSetup();
    }
    /* @AfterClass, @Before, @After are defined in BaseTest */

    @Test
    public void testDistinctCountOnColumn() throws Exception {
        printTestDescription();

        myInitATableValues(tenantId, null);
        String query = "SELECT count(DISTINCT A_STRING) FROM aTable";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(3, rs.getInt(1));
            assertEquals(3, rs.getLong(1));// It should work with getInt() or getLong()
            assertFalse(rs.next());
        } finally {
        }
    }

    @Test
    public void testDistinctCountOnRKColumn() throws Exception {
        printTestDescription();

        myInitATableValues(tenantId, null);
        String query = "SELECT count(DISTINCT ORGANIZATION_ID) FROM aTable";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
            assertEquals(1, rs.getLong(1));// It should work with getInt() or getLong()
            assertFalse(rs.next());
        } finally {
        }
    }

    @Test
    public void testDistinctCountWithGroupBy() throws Exception {
        printTestDescription();

        myInitATableValues(tenantId, null);
        String query = null;
        if (tgtPH()) query = "SELECT A_STRING, count(DISTINCT B_STRING) FROM aTable group by A_STRING";
        else if (tgtSQ()||tgtTR()) query = "SELECT A_STRING, count(DISTINCT B_STRING) FROM aTable group by A_STRING order by 1";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(A_VALUE, rs.getString(1));
            assertEquals(2, rs.getLong(2));
            assertTrue(rs.next());
            assertEquals(B_VALUE, rs.getString(1));
            assertEquals(1, rs.getLong(2));
            assertTrue(rs.next());
            assertEquals(C_VALUE, rs.getString(1));
            assertEquals(1, rs.getLong(2));
            assertFalse(rs.next());
        } finally {
        }
    }

    @Test
    public void testDistinctCountWithGroupByOrdered() throws Exception {
        printTestDescription();

        String tenantId2 = "00D400000000XHP";
        myInitATableValues(tenantId, tenantId2);
        String query = null;
        if (tgtPH()) query = "SELECT organization_id, count(DISTINCT A_STRING) FROM aTable group by organization_id";
        else if (tgtSQ()||tgtTR()) query = "SELECT organization_id, count(DISTINCT A_STRING) FROM aTable group by organization_id order by 1 desc";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            if (tgtPH()) assertEquals(tenantId, rs.getString(1));
            else if (tgtSQ()||tgtTR()) assertEquals(tenantId, rs.getString(1).trim());
            assertEquals(3, rs.getLong(2));
            assertTrue(rs.next());
            if (tgtPH()) assertEquals(tenantId2, rs.getString(1));
            else if (tgtSQ()||tgtTR()) assertEquals(tenantId2, rs.getString(1).trim());
            assertEquals(1, rs.getLong(2));
            assertFalse(rs.next());
        } finally {
        }
    }

    @Test
    public void testDistinctCountOn2Columns() throws Exception {
        printTestDescription();

        myInitATableValues(tenantId, null);
        String query = "SELECT count(DISTINCT A_STRING), count(DISTINCT B_STRING) FROM aTable";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(3, rs.getLong(1));
            assertEquals(2, rs.getLong(2));
            assertFalse(rs.next());
        } finally {
        }
    }
    
    @Test
    public void testDistinctCountONE() throws Exception {
        printTestDescription();

        myInitATableValues(tenantId, null);
        String query = "SELECT count(DISTINCT 1) FROM aTable";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getLong(1));
            assertFalse(rs.next());
        } finally {
        }
    }
    
    @Test
    public void testDistinctCountONEWithEmptyResult() throws Exception {
        printTestDescription();

        myInitATableValues(null, null);
        String query = null;
        if (tgtPH()) query = "SELECT count(DISTINCT 1) FROM aTable";
        else if (tgtSQ()||tgtTR()) query = "SELECT count(DISTINCT 1) FROM aTable order by 1";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals(0, rs.getLong(1));
            assertFalse(rs.next());
        } finally {
        }
    }
    
    protected void myInitATableValues(String tenantId1, String tenantId2) throws Exception {
        Date date = null;

        createTestTable(ATABLE_NAME);
        try {
            // Insert all rows at ts
            PreparedStatement stmt = null;
            if (tgtPH()||tgtTR()) stmt = conn.prepareStatement(
                    "upsert into " +
                    "ATABLE(" +
                    "    ORGANIZATION_ID, " +
                    "    ENTITY_ID, " +
                    "    A_STRING, " +
                    "    B_STRING, " +
                    "    A_INTEGER, " +
                    "    A_DATE, " +
                    "    X_DECIMAL, " +
                    "    X_LONG, " +
                    "    X_INTEGER," +
                    "    Y_INTEGER)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            else if (tgtSQ()) stmt = conn.prepareStatement(
                    "insert into " +
                    "ATABLE(" +
                    "    ORGANIZATION_ID, " +
                    "    ENTITY_ID, " +
                    "    A_STRING, " +
                    "    B_STRING, " +
                    "    A_INTEGER, " +
                    "    A_DATE, " +
                    "    X_DECIMAL, " +
                    "    X_LONG, " +
                    "    X_INTEGER," +
                    "    Y_INTEGER)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            if (tenantId1 != null) {
                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW1);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, B_VALUE);
                stmt.setInt(5, 1);
                stmt.setDate(6, date);
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW2);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 2);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 1));
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW3);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 3);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 2));
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW4);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, B_VALUE);
                stmt.setInt(5, 4);
                stmt.setDate(6, date == null ? null : date);
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW5);
                stmt.setString(3, B_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 5);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 1));
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW6);
                stmt.setString(3, B_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 6);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 2));
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW7);
                stmt.setString(3, B_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 7);
                stmt.setDate(6, date == null ? null : date);
                stmt.setBigDecimal(7, BigDecimal.valueOf(0.1));
                stmt.setLong(8, 5L);
                stmt.setInt(9, 5);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW8);
                stmt.setString(3, B_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 8);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 1));
                stmt.setBigDecimal(7, BigDecimal.valueOf(3.9));
                long l = Integer.MIN_VALUE - 1L;
                assert (l < Integer.MIN_VALUE);
                stmt.setLong(8, l);
                stmt.setInt(9, 4);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId1);
                stmt.setString(2, ROW9);
                stmt.setString(3, C_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 9);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 2));
                stmt.setBigDecimal(7, BigDecimal.valueOf(3.3));
                l = Integer.MAX_VALUE + 1L;
                assert (l > Integer.MAX_VALUE);
                stmt.setLong(8, l);
                stmt.setInt(9, 3);
                stmt.setInt(10, 300);
                stmt.execute();
            }
            if (tenantId2 != null) {
                stmt.setString(1, tenantId2);
                stmt.setString(2, ROW1);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, B_VALUE);
                stmt.setInt(5, 1);
                stmt.setDate(6, date);
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();

                stmt.setString(1, tenantId2);
                stmt.setString(2, ROW2);
                stmt.setString(3, A_VALUE);
                stmt.setString(4, C_VALUE);
                stmt.setInt(5, 2);
                stmt.setDate(6, date == null ? null : new Date(date.getTime() + MILLIS_IN_DAY * 1));
                stmt.setBigDecimal(7, null);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.execute();
            }
        } finally {
        }
    }
}
