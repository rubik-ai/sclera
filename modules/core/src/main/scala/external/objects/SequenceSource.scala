/**
* Sclera - Core
* Copyright 2012 - 2020 Sclera, Inc.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.scleradb.external.objects

import com.scleradb.sql.datatypes.Column
import com.scleradb.sql.types.SqlInteger
import com.scleradb.sql.result.ScalTableResult
import com.scleradb.sql.expr.{ScalColValue, IntConst, ColRef, SortExpr, SortAsc}

/** Generates a sequence of numbers */
private[scleradb]
class SequenceSource(n: Int) extends ExternalSource {
    /** Name of the data source */
    override val name: String = "SEQUENCE"

    /** Name of the column */
    private val colName: String = "N"

    /** Columns of the rows emitted by the data source */
    override val columns: List[Column] = List(Column(colName, SqlInteger))

    /** The rows emitted by the data source */
    override def result: ScalTableResult = {
        val vals: Iterator[Map[String, ScalColValue]] =
            Iterator.range(0, n).map { v => Map(colName -> IntConst(v)) }

        ScalTableResult(columns, vals, List(SortExpr(ColRef(colName), SortAsc)))
    }

    /** String representation */
    override def toString: String = name + "[" + n + "] AS (" + colName + ")"
}

private[scleradb]
object SequenceSource {
    def apply(n: Int): SequenceSource = new SequenceSource(n)
}
