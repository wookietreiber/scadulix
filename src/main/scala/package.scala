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


package object scadulix {

  // -------------------------------------------------------------------
  // aliases
  // -------------------------------------------------------------------

  def currentTime = compat.Platform.currentTime

  type File = java.io.File

  type URI = java.net.URI

  type URL = java.net.URL

  val Properties = scala.util.Properties

  val Source = scala.io.Source

  val EventHandler = akka.event.EventHandler

  // -------------------------------------------------------------------
  // implicit conversions
  // -------------------------------------------------------------------

  implicit def string2File(s: String) = new File(s)
  implicit def string2URI(s: String) = new URI(s)
  implicit def string2URL(s: String) = new URL(s)

  // -------------------------------------------------------------------
  // configuration
  // -------------------------------------------------------------------

  // TODO use envOrElse or propOrElse later
  lazy val         tmpDir = verifiedDir(Properties.tmpDir)
  lazy val sourceCacheDir = verifiedDir("/var/cache/sources")
  lazy val       buildDir = verifiedDir("/usr/src")

  val bar = (s: String) => s match {
    case License.ByOrg(seq @ _*) => seq
    case License.ByName(license) => List(license)
    case s => List(License(s, "https://github.com/login"))
  }

  // Source.fromURL(getClass.getResource("/conf/scadulix-default.conf"))

  def acceptedByConf: Seq[String] = Nil
  def rejectedByConf: Seq[String] = Nil

  def acceptedLicenses: Seq[License] = acceptedByConf flatMap bar
  def rejectedLicenses: Seq[License] = rejectedByConf flatMap bar

  val moduleFilter = (mods: Seq[Module]) => {
    mods/*.view*/ filter { mod =>
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

  def managed[T <: { def close() },U](resource: T)(handle: T => U): U = {
    try {
      return handle(resource)
    } finally {
      resource.close()
    }
  }
}
