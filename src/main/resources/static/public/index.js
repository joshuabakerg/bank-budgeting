/*
 * Copyright Notice
 * ================
 * This file contains proprietary information of Discovery Health.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2020
 */
const formatter = new Intl.NumberFormat('en-ZA', {
    style: 'currency',
    currency: 'ZAR',
});

function fetchWithAuth(url) {
    return fetch(url, {
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("auth_token")
        }
    }).then(response => {
        if (response.status === 401) {
            window.location = "login.html";
        }
        return response;
    });
}

const app = new Vue({
    el: '#app',
    data: {
        selectedPeriod: null,
        periods: [],
        groups: [],
        categories: [],
        transactions: [],
        budgets: [],
        selectedTransactions: []
    },
    methods: {
        async refresh() {
            await app.getTransactions();
            await app.getBudgets();
        },
        async getTransactions() {
            let url = new URL(window.location.origin + '/transactions');
            url.search = new URLSearchParams({start: app.selectedPeriod.start, end: app.selectedPeriod.end}).toString();
            let response = await fetchWithAuth(url);
            let transactions = await response.json();
            app.transactions = transactions;
        },
        async getPeriods() {
            let response = await fetchWithAuth("/periods");
            let periods = await response.json();
            app.selectedPeriod = periods[0];
            app.periods = periods;
        },
        async getGroups() {
            let response = await fetchWithAuth("/groups");
            let groups = await response.json();
            app.groups = groups;
        },
        async getCategories() {
            let response = await fetchWithAuth("/categories");
            let categories = await response.json();
            app.categories = categories;
        },
        async getBudgets() {
            app.budgets = [];
            for (const group of app.groups) {
                let url = new URL(window.location.origin + `/groups/${group.id}/budget`);
                url.search = new URLSearchParams({
                    start: app.selectedPeriod.start,
                    end: app.selectedPeriod.end,
                }).toString();
                let response = await fetchWithAuth(url);
                let budget = await response.json();
                budget.show = false;
                app.budgets.push(budget);
            }
        },
        getPercentage(current, max) {
            if (current > max) {
                return 100;
            } else {
                return Math.floor(current / max * 100);
            }
        },
        getProgressColour(current, max) {
            let percentage = app.getPercentage(current, max);
            if (percentage < 20) {
                return "bg-success";
            } else if (percentage < 70) {
                return "bg-info"
            } else if (percentage < 100) {
                return "bg-warning"
            } else {
                return "bg-danger"
            }
        },
        getCategoryName(categoryId) {
            return app.categories.find(cat => cat.id === categoryId).name;
        },
        getGroupName(groupId) {
            return app.groups.find(group => group.id === groupId).name;
        },
        prettyCurrency(amount) {
            return formatter.format(amount);
        },
        showTransactions(categoryId) {
            console.log(categoryId);
            app.selectedTransactions = app.transactions.filter(trans => trans.categoryId == categoryId);
            $('#myModal').modal('show');
        },
        changePeriods(direction) {
            let index = app.periods.indexOf(app.selectedPeriod);
            let newIndex = index + direction;
            if (newIndex < 0) {
                app.selectedPeriod = app.periods[app.periods.length-1];
            } else if (newIndex >= app.periods.length) {
                app.selectedPeriod = app.periods[0];
            }else{
                app.selectedPeriod = app.periods[newIndex];
            }
            console.log(newIndex);
            console.log(app.selectedPeriod );
            app.refresh();
        }
    }
});

let main = async () => {
    await app.getPeriods();
    await app.getGroups();
    await app.getCategories();
    await app.getTransactions();
    await app.getBudgets();
    console.log("Here");
};

main();
