/*
 * Copyright (c) 2015 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.services


import java.util.zip.ZipOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream
import java.io.BufferedInputStream


/*
*  Usage:
*  
*  scala> import com.snowplowanalytics.services.ZipUtils.jarSlicer
*  scala> jarSlicer("in.zip", "out.zip", "exmaple.txt")
*
*/
object ZipUtils {
     def jarSlicer(originalZipFile:String, newZipFile:String, newModifiedFile:String) {
          val zipFile = new ZipFile(originalZipFile)
          val zos = new ZipOutputStream(new FileOutputStream(newZipFile))
          var e = zipFile.entries()

          // this section rewrites the each file byte by byte back to new zip file
          while(e.hasMoreElements) {
            val entryIn = e.nextElement().asInstanceOf[ZipEntry]
            if (!entryIn.getName.equalsIgnoreCase(newModifiedFile)) {
              zos.putNextEntry(entryIn)
              val is = zipFile.getInputStream(entryIn)
              var b = is.read()
              while (b > -1) {
                zos.write(b)
                b = is.read()
              }
              is.close()
            // this section writes the new file into the new zip file  
            } else {
              zos.putNextEntry(new ZipEntry(newModifiedFile))
              val is = new BufferedInputStream(new FileInputStream(newModifiedFile))
              var b = is.read()
              while (b > -1) {
                zos.write(b)
                b = is.read()
              }
              is.close()
            }
            zos.closeEntry()
          }
          zos.close()
     }
}
