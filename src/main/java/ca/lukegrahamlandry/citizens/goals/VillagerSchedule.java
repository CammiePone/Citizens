package ca.lukegrahamlandry.citizens.goals;

import ca.lukegrahamlandry.citizens.entity.VillagerBase;

import java.util.ArrayList;
import java.util.List;


public class VillagerSchedule {
    // todo: each villager should have thier own instance of this which can be edited in their gui

    // 48 entries. each entry is what to be doing during a half hour chunk
    private final List<VillagerBase.Activity> schedule;

    //     0 - vanilla wake up
    //  2000 - vanilla work day starts
    //  6000 - noon
    //  9000 - vanilla work day ends
    // 12000 - sun set
    // 18000 - midnight

    public VillagerSchedule(){
        this.schedule = new ArrayList<>();

        addActivity(VillagerBase.Activity.HOME, 4);
        addActivity(VillagerBase.Activity.WORK, 14);
        addActivity(VillagerBase.Activity.HOME, 6);
        addActivity(VillagerBase.Activity.SLEEP, 24);
    }

    private void addActivity(VillagerBase.Activity todo, int halfHours){
        for (int i=0;i<halfHours;i++){
            this.schedule.add(todo);
        }
    }

    public VillagerBase.Activity getCurrent(long time){
        int index = (int) Math.floorDiv(time % 24000, 500);
        return this.schedule.get(index);
    }
}
