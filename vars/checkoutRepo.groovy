/**
 * Checks out the a git repository into a subdirectory of the current working directory.
 *
 * @param repository - git repository name, and name of subdirectory
 * @param commitOrBranch - git commit id or branch to checkout
 * @param depth - depth of history to checkout
 *
 * @return nil
 */
def call(String repository, String commitOrBranch, int depth, String mainBranchName = "master", String url = "git@github.com:r2c-CSE") {
        def timeout = 15 // in minutes
        def extensions = [
            [$class: 'RelativeTargetDirectory', relativeTargetDir: repository],
            [$class: 'CheckoutOption', timeout: timeout],
            [$class: 'CloneOption', noTags: true, honorRefspec: true, timeout: timeout, shallow: true, depth: depth],
        ]
        def commitPattern = /^[0-9a-f]{40}$/
        def refspec = ''
        if (commitOrBranch =~ commitPattern) {
            println('Checking out commit')
            refspec = "+refs/heads/${mainBranchName}:refs/remotes/origin/${mainBranchName} ${commitOrBranch}"
        } else if (commitOrBranch != '${mainBranchName}') {
            println('Checking out branch')
            refspec = "+refs/heads/${mainBranchName}:refs/remotes/origin/${mainBranchName} +refs/heads/${commitOrBranch}:refs/remotes/origin/${commitOrBranch}"
        } else {
            println('Checking out master/main only')
            refspec = "+refs/heads/${mainBranchName}:refs/remotes/origin/${mainBranchName}"
        }
        checkout([
          $class : 'GitSCM',
          branches             : [[name: commitOrBranch]],
          doGenerateSubmoduleConfigurations: false,
          extensions               : extensions,
          submoduleCfg             : [],
          userRemoteConfigs        : [[
          url     : "${url}/${repository}",
          refspec : refspec,
          ]]])
}
