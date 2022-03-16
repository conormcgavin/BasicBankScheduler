package finalyearproject;

import java.util.ArrayList;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import java.lang.Math;

public class Controller {
	ArrayList<Employee> employees;
	int week;
	Scheduler scheduler;
	
	public Controller() {
		this.employees = new ArrayList<Employee>();
		this.week = 0;
	}
	
	public void addEmployee(Employee e) {
		System.out.printf("Adding Employee %s...\n", e.name);
		this.employees.add(e);
	}
	
	public void removeEmployee(Employee e) {
		// this could cause huge glitches due to index stuff ?
		System.out.printf("Removing Employee %s\n...", e.name);
		this.employees.remove(e);
	}
	
	public Scheduler initialiseScheduler() {
		System.out.println("Initialising Scheduler...");
		this.scheduler = new Scheduler(this.employees);
		return scheduler;
	}
	
	public void addPreferencesToModel(Scheduler scheduler) {
		System.out.println("Adding employee requests and preferences to model...");
		for (int i = 0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			ArrayList<Preference> prefs = e.getPreferences();
			for (Preference pref : prefs) {
				scheduler.addPreference(i, pref);
			}
		}
	}
	
	public void updateBanks() {
		System.out.println("Updating Banks...");
		int total = 0;
		int sum[] = new int[employees.size()];
		for (int i = 0; i < employees.size(); i++) {
			IntVar[] iv = scheduler.preferences.get(i);
			for (int j=0; j<iv.length; j++) {
				if (iv[j].getValue() == 1) {
					sum[i] += 1;
					total += 1;
				}
			}
		}
		float avg = (float)total / employees.size();
		int iavg = Math.round(avg*100);
		System.out.println(iavg + " " + avg);
		for (int i=0; i<sum.length; i++) {
			if (sum[i] < avg) {
				employees.get(i).addBank((iavg-(sum[i]*100)));
			} else if (sum[i] > avg) {
				employees.get(i).removeBank(((sum[i]*100)-iavg));
			}
		}
	}
	
	
	public void recordHistoryOfCurrentWeek() {
		System.out.println("Recording History...");
		
		for (int employee = 0; employee < employees.size(); employee ++) {
			Employee e = employees.get(employee);
			
			for (int i=0; i<e.preferences.size(); i++) {
				Preference pref = e.preferences.get(i);
				if (scheduler.preferences.get(employee)[i].getValue() == 1) {
					pref.granted = true;
					e.preferences_received[i] += 1;
				}
				Preference pref_copy = Preference.copy(pref);
				e.preference_history.add(pref_copy);
			}
			
			e.total_given_score += scheduler.preference_score_assignment_after_solving[employee].getValue();
			e.bank_history.add(e.bank);
			e.pref_score_history.add(scheduler.preference_score_assignment_after_solving[employee].getValue());
		}
	}
	

	public Solution runWeek() {
		System.out.println("********************************************************************************");
		System.out.printf("-----------------------------RUNNING WEEK %d--------------------------------\n", week);
		addPreferencesToModel(this.scheduler);
		scheduler.optimise();
		Solution solution = scheduler.solve();
		return solution;
	}
	
	public void completeWeek() {
		updateBanks();
		recordHistoryOfCurrentWeek();
		System.out.println("********************************************************************************\n");
	}
	
	public void newWeek() {
		System.out.println("Starting preparation for a new week...");
		week += 1;
		for (Employee e : employees) {
			ArrayList<Preference> prefs = e.getPreferences();
			for (Preference p : prefs) {
				p.score = p.calculateScore(e.bank);
				p.week = week;
				p.granted = false;
			}
		}
		scheduler = initialiseScheduler();
	}
}