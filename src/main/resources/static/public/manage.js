const app = new Vue({
    el: '#app',
    data: {
        user: {
            loggedIn: false,
            name: undefined,
            surname: undefined,
            username: undefined
        }
    },
    methods: {
        retrieveUser: async () => {
            let user = await fetch('/api/accounts/me', {
                method: 'GET'
            }).then(res => res.json());
            app.user.name = user.name;
            app.user.surname = user.surname;
            app.user.email = user.email;
            app.user.loggedIn = true;
        }
    }
});

let main = async () => {
    await app.retrieveUser()
};

main();
