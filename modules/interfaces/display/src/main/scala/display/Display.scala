/**
* Sclera - Display
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

package com.scleradb.interfaces.display

/**
 * Display base class
 */
abstract class Display {
    /**
     * Start the display
     */
    def start(): Unit

    /**
     * Submit message (specification, data, etc.)
     * @param message Message to submit
     */
    def submit(message: String): Unit

    /**
     * Stop the display
     */
    def stop(): Unit
}
