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


package scadulix

/** Holds [[scadulix.License]] objects. */
object License extends util.Values {
  import Organisation._

  object ByOrg {
    def unapplySeq(organisation: String) = byOrganisation(organisation)
  }

  object ByName {
    def unapply(name: String): Option[License] = byName(name)
  }

  def byOrganisation(name: String): Option[Seq[License]] =
    Organisation.byName[Organisation](name) map { org =>
      values[License] filter { _.approvers.contains(org) } toList
    }

  /** Returns latest GPL. */
  lazy val GPL = GPLv3

  lazy val GPLv3 = License("GNU General Public License version 3.0",
    "http://www.gnu.org/licenses/gpl.txt", OSI)

  lazy val GPLv2 = License("GNU General Public License version 2.0",
    "http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt", OSI)

  /** Returns latest LGPL. */
  lazy val LGPL = LGPLv3

  lazy val LGPLv3 = License("GNU Lesser General Public License version 3.0",
    "http://www.gnu.org/licenses/lgpl.txt", OSI)

  lazy val LGPLv2 = License("GNU Lesser General Public License version 2.1",
    "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt", OSI)
}

/** Software license.
  *
  * @param name      Returns the name of this license.
  * @param website   Returns the website containing the full text of this license.
  * @param approvers Returns the organisations approving this license.
  */
case class License(name: String, website: URL, approvers: Organisation*) {
  /** Returns the full text of this license. */
  def content = managed(Source fromURL website) {
    _.getLines mkString "\n"
  }

  /** Returns the name of this license. */
  override def toString = name
}
