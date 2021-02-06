module.exports = {
    webpackFinal: async (config, { configType }) => {
        config.optimization = {
          minimize: false,
          minimizer: [],
        };
    return config;
  },

  babel: async (options) => {
//      console.log("babel options: ", options)
      return {
        ...options,
        ignore: [
          /.*com\.fulcrologic.*/,
          /.*cljs.*js/,
          /.*goog.*\.js/
        ]
      }
  },
  stories: ["../resources/public/stories/js/**/*_story.js"
            //  , "../src/stories/**/*.stories.js"
  ],
  addons: ["@storybook/addon-actions", "@storybook/addon-links"],
}
