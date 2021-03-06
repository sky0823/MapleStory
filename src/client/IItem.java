/*
 	OrpheusMS: MapleStory Private Server based on OdinMS
    Copyright (C) 2012 Aaron Weiss

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

public interface IItem extends Comparable<IItem> {
	public final int PET = 3;
	public final int ITEM = 2;
	public final int EQUIP = 1;

	public void setFlag(byte b);

	byte getFlag();

	byte getType();

	byte getPosition();

	void setPosition(byte position);

	int getItemId();

	int getCashId();

	short getQuantity();

	String getOwner();

	int getPetId();

	void setPetId(int id);

	IItem copy();

	void setOwner(String owner);

	void setQuantity(short quantity);

	long getExpiration();

	void setExpiration(long expiration);

	void setSN(int sn);

	int getSN();

	String getGiftFrom();

	void setGiftFrom(String giftFrom);

	MaplePet getPet();
}
