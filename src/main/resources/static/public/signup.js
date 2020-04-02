const app = new Vue({
    el: '#app',
    data: {
        form: {
            name: "",
            surname: "",
            email: "",
            password: ""
        }
    },
    methods: {
        signup: async () => {
            const rawResponse = await fetch('/api/accounts/signup', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(app.form)
            });
            if (rawResponse.ok) {
                console.log("ok");
                window.location = "login.html";
            } else {
                console.log("not ok")
            }
            console.log(rawResponse);
        }
    }
});

let main = async () => {
};

main();
