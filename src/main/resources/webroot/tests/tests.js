import stepView from "../step-view.js";
import modelView from "../model-view.js";

import { mount } from "vue-test-utils";

export const stepViewTest = mount(stepView, {
    props: {
        step: {
            name: "test",
            method: "GET",
            path: "/test"
        }
    }
});

export const modelViewTest = mount(modelView, {
    data() {
        return {
            model: {
                client: {
                    topology: {
                        local: {
                            parallel: 7,
                            repeat: 5
                        }
                    }
                },
                results: {format: "json", filename: "sample.json"}
            },
            /**
             * Can be Running or Stopped.
             * @type {boolean}
             */
            running: false,
            reportType: {},
            runningReportName: null
        };
    }
});
