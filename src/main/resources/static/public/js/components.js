/*
 * Copyright Notice
 * ================
 * This file contains proprietary information of Discovery Health.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2020
 */

Vue.component('user-tag', {
    data: function () {
        return {
            user: null
        }
    },
    mounted: async function () {
        this.user = await fetch('/api/user').then(res => res.json());
        console.log(this.user);
    },
    template: `
<div style="float: right">
    <span v-if="user" id="usernameitem" class="help-block" style="font-style:italic;">Hi, {{user.firstName}}</span>
</div>`
});