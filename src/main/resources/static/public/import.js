const app = new Vue({
    el: '#app',
    data: {
        selectedFiles: null,
        selectedFileName: '',
        loading: false
    },
    methods: {
        fileChanged(event) {
            console.log(event.target.files);
            app.selectedFiles = event.target.files;
            app.selectedFileName = app.selectedFiles[0].name;
        },
        async uploadFile() {
            if (app.selectedFiles) {
                app.loading = true;
                let formData = new FormData();
                for (const selectedFile of app.selectedFiles) {
                    formData.append(selectedFile.name, app.selectedFiles.item(0));
                }
                console.log(formData);
                let response = await fetch('/transactions/import', {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("auth_token")
                    },
                    method: 'POST',
                    body: formData
                });
                let json = await response.json();
                app.loading = false;
                if (!response.ok) {
                    app.uploadStatus = json.detail;
                }
            }
        },
    }
});

let main = async () => {
};

main();
