const app = new Vue({
    el: '#app',
    data: {
        form: {
            username: "",
            password: ""
        }
    },
    methods: {
        login: async () => {
            const rawResponse = await fetch('/api/authenticate', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(app.form)
            });
            if (rawResponse.ok) {
                console.log("authenticated");
                let response = await rawResponse.json();
                let token = response.token;
                localStorage.setItem("auth_token", token);
                window.location = "index.html";
            } else {
                console.log("Authentication failed");
                console.log(rawResponse);
            }
        }
    }
});

let main = async () => {
};

main();
