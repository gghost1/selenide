task_branch = "${TEST_BRANCH_NAME}"
def brunch_cutted = task_branch.contains("origin/") ? task_branch.split("origin/")[1] : task_branch.trim()
currentBuild.displayName = "$brunch_cutted"

base_git_url = "https://github.com/gghost1/selenide.git"

node {
    withEnv(["branch=${brunch_cutted}", "base_url=${base_git_url}"]) {
        stage('Checkout Branch') {
            if (!"$branch_cutted".contains("master")) {
                try {
                    getProject("$base_url", "$branch_cutted")
                } catch (Exception e) {
                    echo "Branch $branch_cutted not found"
                    throw ("$e")
                }
            } else {
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
    testStages.each {
        tag -> stages["${tag}"] = {
            returnTestWithTag(tag)
        }
    }
    return stages
}

def returnTestWithTag(String tag) {
    try {
        labelledShell(label: "Run $tag", script: "mvn clean test -Dtest=$tag")
    } catch (Exception e) {
        echo "Test $tag failed"
    }
}

def getProject(String repo, String branch) {
    cleanWs()
    checkout scm: [
            $class: 'GitSCM',
            branches: [[name: branch]],
            userRemoteConfigs: [[url: repo]]
    ]
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