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
        dateStart: "2019-10-25",
        dateEnd: "2019-11-24",
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
            let url = new URL('http://' + window.location.host + '/transactions');
            url.search = new URLSearchParams({start: app.dateStart, end: app.dateEnd}).toString();
            let response = await fetchWithAuth(url);
            let transactions = await response.json();
            app.transactions = transactions;
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
                let url = new URL('http://' + window.location.host + `/groups/${group.id}/budget`);
                url.search = new URLSearchParams({
                    start: app.dateStart,
                    end: app.dateEnd,
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
        }
    }
});

let main = async () => {
    await app.getGroups();
    await app.getCategories();
    await app.getTransactions();
    await app.getBudgets();
    console.log("Here");
};

main();
