/*
 	OrpheusMS: MapleStory Private Server based on OdinMS
    Copyright (C) 2012 Aaron Weiss <aaron@deviant-core.net>
    				Patrick Huy <patrick.huy@frz.cc>
					Matthias Butz <matze@odinms.de>
					Jan Christian Meyer <vimes@odinms.de>

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
package net.server.handlers.channel;

import java.util.ArrayList;
import java.util.List;
import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.status.MonsterStatusEffect;
import net.AbstractMaplePacketHandler;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.maps.MapleSummon;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class SummonDamageHandler extends AbstractMaplePacketHandler {
	public final class SummonAttackEntry {

		private int monsterOid;
		private int damage;

		public SummonAttackEntry(int monsterOid, int damage) {
			this.monsterOid = monsterOid;
			this.damage = damage;
		}

		public int getMonsterOid() {
			return monsterOid;
		}

		public int getDamage() {
			return damage;
		}
	}

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int oid = slea.readInt();
		MapleCharacter player = c.getPlayer();
		if (!player.isAlive()) {
			return;
		}
		MapleSummon summon = null;
		for (MapleSummon sum : player.getSummons().values()) {
			if (sum.getObjectId() == oid) {
				summon = sum;
			}
		}
		if (summon == null) {
			return;
		}
		ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
		MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
		slea.skip(4);
		List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
		byte direction = slea.readByte();
		int numAttacked = slea.readByte();
		slea.skip(8); // Thanks Gerald :D, I failed lol (mob x,y and summon x,y)
		for (int x = 0; x < numAttacked; x++) {
			int monsterOid = slea.readInt(); // attacked oid
			slea.skip(18);
			int damage = slea.readInt();
			allDamage.add(new SummonAttackEntry(monsterOid, damage));
		}
		player.getMap().broadcastMessage(player, MaplePacketCreator.summonAttack(player.getId(), summon.getSkill(), direction, allDamage), summon.getPosition());
		for (SummonAttackEntry attackEntry : allDamage) {
			int damage = attackEntry.getDamage();
			MapleMonster target = player.getMap().getMonsterByOid(attackEntry.getMonsterOid());
			if (target != null) {
				if (damage > 0 && summonEffect.getMonsterStati().size() > 0) {
					if (summonEffect.makeChanceResult()) {
						target.applyStatus(player, new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill, null, false), summonEffect.isPoison(), 4000);
					}
				}
				player.getMap().damageMonster(player, target, damage);
			}
		}
	}
}