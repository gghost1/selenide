def mvnHome = tool name: 'Maven', type: 'maven'

node {
    withEnv(["branch=${branch_cutted}", "base_url=${base_git_url}"]) {
        stage('Checkout Branch') {
            script {
                if (!env.branch.contains("master")) {
                    checkout([
                            $class: 'GitSCM',
                            branches: [[name: env.branch]],
                            extensions: [],
                            userRemoteConfigs: [[url: env.base_url]]
                    ])
                } else {
                    checkout scm
                }
            }
        }

        stage('Run Tests') {
            try {
                parallel getTestStages(["ImageTest"])
            } finally {
                generateAllure()
            }
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
        sh "${mvnHome}/bin/mvn clean test -Dtest=${tag}"
    }
}