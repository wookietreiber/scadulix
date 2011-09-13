/* **************************************************************************
 *                                                                          *
 *  Copyright (C)  2011  Christian Krause                                   *
 *                                                                          *
 *  Christian Krause <kizkizzbangbang@googlemail.com>                       *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  This file is part of 'scadulix'.                                        *
 *                                                                          *
 *  This project is free software: you can redistribute it and/or modify    *
 *  it under the terms of the GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or       *
 *  any later version.                                                      *
 *                                                                          *
 *  This project is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this project. If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                          *
 ****************************************************************************/


import scala.util.Properties
import akka.config.Configuration

package object scadulix {

  // -------------------------------------------------------------------
  // aliases
  // -------------------------------------------------------------------

  def currentTime = compat.Platform.currentTime

  type File = java.io.File

  type URI = java.net.URI

  type URL = java.net.URL

  val Source = scala.io.Source

  val EventHandler = akka.event.EventHandler

  // -------------------------------------------------------------------
  // configuration
  // -------------------------------------------------------------------

  /** Returns the current configuration. */
  def conf = Configuration fromFile {
    Properties propOrElse("scadulix.conf", "/etc/scadulix/scadulix.conf")
  }

  /** Returns the temporary file directory. */
  def tmpDir = verifiedDir {
    conf getString ( "scadulix.tmpDir", Properties.tmpDir )
  }

  /** Returns the base prefix directory. */
  def prefix = verifiedDir {
    conf getString ( "scadulix.prefix", "/usr" )
  }

  /** Returns the base cache directory. */
  def cacheDir = verifiedDir {
    conf getString ( "scadulix.cacheDir", "/var/cache" )
  }

  /** Returns the source cache directory. */
  def sourceCache = verifiedDir {
    conf getString ( "scadulix.sourceCache", cacheDir + "/sources" )
  }

  /** Returns the directory where modules are built. */
  def buildDir = verifiedDir {
    conf getString ( "scadulix.buildDir", prefix + "/src" )
  }

  /** Optionally returns licenses by name and by organisation. */
  private val licenseExtractors = (s: String) => s match {
    case License.ByName(license) => List(license)
    case License.ByOrg(seq @ _*) => seq
  }

  /** Returns accepted licenses. */
  def acceptedLicenses: Seq[License] =
    conf getList "scadulix.licenses.accepted" flatMap licenseExtractors

  /** Returns rejected licenses. */
  def rejectedLicenses: Seq[License] =
    conf getList "scadulix.licenses.rejected" flatMap licenseExtractors

  /** Filters the modules by accepted and rejected licenses. */
  val licenseFilter = (mods: Seq[Module]) => {
    mods filter { mod =>
      acceptedLicenses contains mod.licenses
    } filterNot { mod =>
      rejectedLicenses contains mod.licenses
    }
  }

  // -------------------------------------------------------------------
  // resources
  // -------------------------------------------------------------------

  /** Returns given [[scadulix.File]] if it is an existing directory.
    *
    * The directory will be created if it does not exist and this method
    * will fail if unable to do so.
    */
  def verifiedDir(dir: File) = {
    if (dir.exists)
      if (dir.isDirectory)
        dir
      else
        sys.error(dir + " exists but is not a directory.")
    else
      if (dir.mkdirs) {
        EventHandler.info(this, dir + " creation successful.")
        dir
      } else
        sys.error(dir + " creation failed.")
  }

  /** Closes the resource after handling it. */
  def managed[A <: { def close() },B](resource: A)(handle: A => B) = {
    try {
      handle(resource)
    } finally {
      resource.close()
    }
  }

  // -------------------------------------------------------------------
  // implicit conversions
  // -------------------------------------------------------------------

  implicit def string2File(s: String): File = new File(s)
  implicit def string2URI(s: String): URI = new URI(s)
  implicit def string2URL(s: String): URL = new URL(s)

}
