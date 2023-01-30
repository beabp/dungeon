package org.example;

import game.character.Creature;
import game.character.Wizard;
import game.conditions.*;
import game.demiurge.Demiurge;
import game.demiurge.DemiurgeEndChecker;
import game.dungeon.Dungeon;
import game.dungeon.Home;
import game.dungeon.Room;
import game.object.Item;
import game.object.Necklace;
import game.object.Ring;
import game.object.Weapon;
import game.objectContainer.Container;
import game.objectContainer.Wearables;
import game.spell.Spell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

public class MainSaveXML {
    public static void main(String[] args) {
        File file = new File("");
        MainSaveXML save = new MainSaveXML();
        DEMIURGOEJEMPLO ejemplo = new DEMIURGOEJEMPLO();
        Demiurge demiurge = ejemplo.getDemiurgoDeEjemplo();
        save.writeFile(demiurge, file);
    }

    private void writeFile(Demiurge demiurge, File file) {
        Path filePath = Paths.get("data/prueba.xml");
//        Path filePath = file.getPath();
        StringBuilder demiurgeBuilder = new StringBuilder();
        String home = saveHome(demiurge);
        String rooms = saveRooms(demiurge);
        String wizard = saveWizard(demiurge);

        String conditions = saveConditions(demiurge);
        String day = saveDay(demiurge);
        String endOfFine = "</demiurge>";
        demiurgeBuilder.append(home)
                .append(rooms)
                .append(wizard)
                .append(conditions)
                .append(day)
                .append(endOfFine);
        String demiurgeStr = demiurgeBuilder.toString();
        try {
            Files.newBufferedWriter(filePath).flush();
            Files.write(filePath, demiurgeStr.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private String saveDay(Demiurge demiurge) {
        String day = String.valueOf(demiurge.getDay());
        StringBuilder dayBuilder = new StringBuilder();
        dayBuilder.append("<day value=\"")
                .append(day)
                .append("\"/>");
        return dayBuilder.toString();
    }

    private String saveConditions(Demiurge demiurge) {
        List<Condition> conditionList = demiurge.getEndChecker().getConditions();
        StringBuilder conditionsBuilder = new StringBuilder();
        String startConditions = "<conditions>";
        String endConditions = "</conditions>";


        conditionsBuilder.append(startConditions);
        conditionList.forEach(condition -> {
            if (!(condition instanceof SimpleCondition)) {
                conditionsBuilder.append("<condition type=\"");
                if (condition instanceof FindCreatureCondition) {
                    conditionsBuilder.append("FindCreature");
                } else if (condition instanceof FindObjectCondition) {
                    conditionsBuilder.append("FindObject");
                } else if (condition instanceof KillAllCreaturesCondition) {
                    conditionsBuilder.append("KillAllCreatures");
                } else if (condition instanceof VisitAllRoomsCondition) {
                    conditionsBuilder.append("VisitAllRooms");
                }
                conditionsBuilder.append("\"/>");
            }
        });

        conditionsBuilder.append(endConditions);
        return conditionsBuilder.toString();
    }

    private String saveHome(Demiurge demiurge) {
        Home home = demiurge.getHome();
        String description = "<description>" + home.getDescription() + "</description>";
        String comfort = "<comfort>" + home.getComfort() + "</comfort>";
        String singa = "<singa><currentValue>" + home.getSinga() + "</currentValue><maxValue>" +
                +home.getMaxSinga() + "</maxValue></singa>";

        StringBuilder itemBuilder = new StringBuilder();
        itemBuilder.append("<chest><capacity>")
                .append(home.getContainer().getValue())
                .append("</capacity><items>");

        Iterator<Item> items = home.getContainer().iterator();
        items.forEachRemaining(item -> {
            itemBuilder.append("<item type=\"");
            if (item instanceof Weapon) {
                itemBuilder.append("weapon");
            } else if (item instanceof Necklace) {
                itemBuilder.append("necklace");
            } else if (item instanceof Ring) {
                itemBuilder.append("ring");
            }
            itemBuilder.append("\"><domain element=\"")
                    .append(item.getDomain())
                    .append("\"/>")
                    .append("<level value=\"")
                    .append(item.getValue())
                    .append("\"/></item>");
        });
        itemBuilder.append("</items></chest>");
        String chest = itemBuilder.toString();

        Iterator<Spell> spells = home.getLibrary().iterator();
        StringBuilder libraryBuilder = new StringBuilder();
        libraryBuilder.append("<library><spells>");
        spells.forEachRemaining(spell -> {
            libraryBuilder.append("<spell><domain element=\"")
                    .append(spell.getDomain())
                    .append("\"/>")
                    .append("<level value=\"")
                    .append(spell.getLevel())
                    .append("\"/></spell>");
        });
        libraryBuilder.append("</spells></library>");
        String library = libraryBuilder.toString();


        return "<demiurge><dungeon><home>" +
                description +
                comfort +
                singa +
                chest +
                library +
                "</home>";
    }

    private String saveRooms(Demiurge demiurge) {
        Dungeon dungeon = demiurge.getDungeon();
        StringBuilder roomListBuilder = new StringBuilder();
        Iterator<Room> rooms = dungeon.iterator();
        rooms.forEachRemaining(room -> {
            StringBuilder roomBuilder = new StringBuilder();
            String id = "<id>" + room.getID() + "</id>";
            String exit = "<exit>" + room.isExit() + "</exit>";
            String description = "<description>" + room.getDescription() + "</description>";
            String visited = "<visited>" + room.isVisited() + "</visited>";
            StringBuilder itemBuilder = new StringBuilder();
            itemBuilder.append("<items>");
            Iterator<Item> items = room.getContainer().iterator();
            items.forEachRemaining(item -> {
                itemBuilder.append("<item type=\"");
                if (item instanceof Weapon) {
                    itemBuilder.append("weapon");
                } else if (item instanceof Necklace) {
                    itemBuilder.append("necklace");
                } else if (item instanceof Ring) {
                    itemBuilder.append("ring");
                }
                itemBuilder.append("\"><domain element=\"")
                        .append(item.getDomain())
                        .append("\"/>")
                        .append("<level value=\"")
                        .append(item.getValue())
                        .append("\"/></item>");

            });
            itemBuilder.append("</items>");
            String itemList = itemBuilder.toString();

            StringBuilder creatureBuilder = new StringBuilder();
            creatureBuilder.append("<creatures>");
            if (room.getCreature() != null) {
                String name = "<name>" + room.getCreature().getName() + "</name>";
                String life = "<life>" + room.getCreature().getLife() + "</life>";
                String punch = "<punch>" + room.getCreature().getRandomAttack().getDamage() + "</punch>";
                creatureBuilder.append("<creature type=\"")
                        .append(room.getCreature().getDomain())
                        .append("\">")
                        .append(name)
                        .append(life)
                        .append(punch);
                Iterator<Spell> spells = room.getCreature().getMemory().iterator();
                StringBuilder spellsBuilder = new StringBuilder();
                spellsBuilder.append("<spells>");
                spells.forEachRemaining(spell -> {
                    spellsBuilder.append("<spell><domain element=\"")
                            .append(spell.getDomain())
                            .append("\"/>")
                            .append("<level value=\"")
                            .append(spell.getLevel())
                            .append("\"/></spell> </spells> </creature> </creatures>");
                });
                String spellsList = spellsBuilder.toString();
                creatureBuilder.append(spellsList);
            } else {
                creatureBuilder.append("</creatures>");
            }
            String creatures = creatureBuilder.toString();

            roomBuilder.append("<room>")
                    .append(id)
                    .append(exit)
                    .append(description)
                    .append(visited)
                    .append(itemList)
                    .append(creatures)
                    .append("</room>");
            roomListBuilder.append(roomBuilder);
        });
        String roomList = roomListBuilder.toString();
        return "<rooms>" +
                roomList +
                "</rooms></dungeon>";

    }


    private String saveWizard(Demiurge demiurge) {
        Wizard wizard = demiurge.getWizard();
        StringBuilder wizardXML;
        //nombre
        wizardXML = new StringBuilder("<wizard>" +
                "<name>" + wizard.getName() + "</name>");

        //hp
        wizardXML.append("<life>")
                .append("<currentValue>").append(wizard.getLife()).append("</currentValue>")
                .append("<maxValue>").append(DungeonLoaderManualXML.INITIAL_LIFE_MAX).append("</maxValue>")
                .append("</life>");

        //energy
        wizardXML.append("<energy>")
                .append("<currentValue>").append(wizard.getEnergy()).append("</currentValue>")
                .append("<maxValue>").append(DungeonLoaderManualXML.INITIAL_ENERGY_MAX).append("</maxValue>")
                .append("</energy>");

        //crytalCarrier
        wizardXML.append("<crystalCarrier>")
                .append("<capacity>").append(wizard.getCrystalCarrier().getValue()).append("</capacity>")
                .append("<crystals>");
        Iterator<Item> itemCrystalCarrier = wizard.getCrystalCarrier().iterator();
        itemCrystalCarrier.forEachRemaining(item -> {
            wizardXML.append("<crystal>")
                    .append("<singa>").append(item.getValue()).append("</singa>")
                    .append("</crystal>");
        });
        wizardXML.append("</crystals>")
                .append("</crystalCarrier>");

        //weareables
        wizardXML.append("<weareables>")
                .append("<weaponsMax>").append(DungeonLoaderManualXML.MAX_WEAPONS).append("</weaponsMax>")
                .append("<necklacesMAX>").append(DungeonLoaderManualXML.MAX_NECKLACES).append("</necklacesMAX>")
                .append("<ringsMAX>").append(DungeonLoaderManualXML.MAX_RINGS).append("</ringsMAX>")
                .append("<items>");
        StringBuilder itemBuilder = new StringBuilder();
        Iterator<Item> itemWearables = wizard.getWearables().iterator();
        itemWearables.forEachRemaining(item -> {
            if (item instanceof Weapon) {
                itemBuilder.append("weapon");
            } else if (item instanceof Necklace) {
                itemBuilder.append("necklace");
            } else if (item instanceof Ring) {
                itemBuilder.append("ring");
            }
            wizardXML.append("<item type=\"").append(itemBuilder).append("\">")
                    .append("<domain element=\"").append(item.getDomain()).append("\"/>")
                    .append("<level value=\"").append(item.getValue()).append("\"/>")
                    .append("</item>");
        });
        wizardXML.append("</items>")
                .append("</weareables>");
        //jewelryBag
        wizardXML.append("<jewelryBag>")
                .append("<capacity>").append(wizard.getJewelryBag().getValue()).append("</capacity>")
                .append("<items>");

        Iterator<Item> itemJewelry = wizard.getJewelryBag().iterator();
        StringBuilder itemBuilder2 = new StringBuilder();;
        itemJewelry.forEachRemaining(item -> {
            if (item instanceof Necklace) {
                itemBuilder2.append("necklace");
            } else if (item instanceof Ring) {
                itemBuilder2.append("ring");
            }
            wizardXML.append("<item type=\"").append(itemBuilder2).append("\">")
                    .append("<domain element=\"").append(item.getDomain()).append("\"/>")
                    .append("<level value=\"").append(item.getValue()).append("\"/>")
                    .append("</item>");
        });
        wizardXML.append("</items>")
                .append("</jewelryBag>")
                .append("</wizard>");

        return wizardXML.toString();
    }
}
