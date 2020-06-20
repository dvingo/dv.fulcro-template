const { resolve } = require('path')
const storybookBuildDir = resolve(__dirname, '..', 'builds', 'storybook-build')

module.exports = {
  stories: ['../storybook-build/*_stories.js'],
  addons: ['@storybook/addon-actions', '@storybook/addon-links'],
  webpackFinal: (config) => {
    // This renders the webpack config for storybook, if you want to make more changes.

    console.dir(config, {depth: null})

    // This assumes the babel loader is in position. TODO use filter to find the
    // index.
    config.module.rules[0].exclude = [
      resolve(__dirname, 'node_modules'),
      // Don't have babel try to transpile our compiled code. Bad things will
      // happen like OOM errors that crash V8.
      storybookBuildDir
    ]
    return config
  }
}
