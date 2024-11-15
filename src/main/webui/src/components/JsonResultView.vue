<script lang="ts">
import { defineComponent } from 'vue'
import api, { ResourceType, type AssertionResult } from '../api'

const options: Intl.DateTimeFormatOptions = {
    hour: "numeric",
    minute: "numeric",
    second: "numeric",
    //@ts-ignore
    fractionalSecondDigits: 3
};
const timeFormat = new Intl.DateTimeFormat("en-US", options);

export default defineComponent({
    data() {
        return {
        }
    },
    created() {
        api.watch(ResourceType.JSONResult, this.result.name, (msg) => {
            console.log(`Received jsonresults update: ${msg}`);
            if (msg.data != null) {
                this.$emit('updateResult', msg.data);
            }
        });

    },
    unmounted() {
        api.stopWatch(ResourceType.JSONResult, this.result.name);
    },
    props: ['result'],
    emits: ['updateResult'],
    methods: {
        formatStartTime(start) {
            const time = new Date(start);
            return timeFormat.format(time);
        },
        formatDuration(start, end) {
            return new Date(end).valueOf() - new Date(start).valueOf();
        },
        /**
         * Create the text for assertions
         * @param {Array} assertions 
         */
        formatAssertions(assertions: AssertionResult[]) {
            const size = assertions.length;
            const passed = assertions.filter(a => a.passed).length;
            return `${passed}/${size}`;
        },
        /**
         * Create css style for an array of assertions
         * @param {Array} assertions 
         */
        styleAssertions(assertions: AssertionResult[]) {
            const failed = assertions.filter(a => !a.passed).length;
            if (failed > 0) {
                return { backgroundColor: 'red' };
            } else {
                return { backgroundColor: 'green' };
            }
        }
    }
})
</script>

<!-- Template -->
<template>
    <div class="jsonResultsView">
        <div>
            <p><b>Name:</b> {{ result.name }}</p>
            <p><b>Creation Time:</b> {{ result.creationTime }}</p>
            <p><b>Total Duration:</b> {{ formatDuration(result?.summary?.startTime, result?.summary?.endTime) }}</p>
            <p><b>Total Size:</b> {{ result?.summary?.size }}</p>

        </div>
        <DataTable :value="result.results" paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50, 100]" tableStyle="min-width: 50rem">
            <Column field="clientId" header="Client ID"></Column>
            <Column field="stepName" header="Step Name"></Column>
            <Column header="Start Time">
                <template #body="slotProps">
                    {{ formatStartTime(slotProps.data.startTime) }}
                </template>
            </Column>
            <Column header="Duration (ms)">
                <template #body="slotProps">
                    {{ formatDuration(slotProps.data.startTime, slotProps.data.endTime) }}
                </template>
            </Column>
            <Column field="statusCode" header="HTTP Status"></Column>
            <Column header="Assertions (Passed/Total)">
                <template #body="slotProps">
                    <div :style="styleAssertions(slotProps.data.assertions)">
                    {{ formatAssertions(slotProps.data.assertions) }}
                    </div>
                </template>
            </Column>
        </DataTable>
    </div>
</template>


<!-- Style -->
<style scoped></style>
