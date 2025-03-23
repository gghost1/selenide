def task_branch = "${TEST_BRANCH_NAME}"
def branch_cutted = task_branch.contains("origin/") ? task_branch.split("origin/")[1] : task_branch.trim()
currentBuild.displayName = "${branch_cutted}"

def base_git_url = "https://github.com/gghost1/selenide.git"

node {
    withEnv(["branch=${branch_cutted}", "base_url=${base_git_url}"]) {
        stage('Checkout Branch') {
            if (!env.branch.contains("master")) {
                try {
                    getProject("${base_git_url}", "${env.branch}")
                } catch (Exception e) {
                    echo "Branch ${env.branch} not found"
                    currentBuild.result = 'FAILURE'
                    error(e.toString())
                }
            } else {
                getProject("${base_git_url}", "master")
                echo "Checkout master branch"
            }
        }

        try {
            parallel getTestStages(["ImageTest"])
        } finally {
            generateAllure()
        }
    }
}

def getTestStages(testTags) {
    def stages = [:]
    testTags.each { tag ->
        stages["${tag}"] = {
            returnTestWithTag(tag)
        }
    }
    return stages
}

def returnTestWithTag(String tag) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        labelledShell(label: "Run ${tag}", script: "mvn clean test -Dtest=${tag}")
    }
}

def getProject(String repo, String branch) {
    checkout([
            $class: 'GitSCM',
            branches: [[name: branch]],
            extensions: [],
            userRemoteConfigs: [[url: repo]]
    ])
}

def generateAllure() {
    allure([
            includeProperties: true,
            jdk: '',
            properties: [],
            reportBuildPolicy: 'ALWAYS',
            results: [[path: 'target/allure-results']]
    ])
}