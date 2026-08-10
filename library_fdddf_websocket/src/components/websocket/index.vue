<template>
  <div :class="$style.root">
    <h3>WebSocket with STOMP</h3>
    <div>
      <button @click="connect" :disabled="isConnected">Connect</button>
      <button @click="disconnect" :disabled="!isConnected">Disconnect</button>
    </div>
    <div v-if="isConnected">
      <h4>Send a Message:</h4>
      <input v-model="message" placeholder="Type your message"/>
      <button @click="sendMessage">Send</button>
    </div>
    <div v-if="receivedMessages.length">
      <h4>Received Messages:</h4>
      <ul>
        <li v-for="(msg, index) in receivedMessages" :key="index">{{ msg }}</li>
      </ul>
    </div>
  </div>
  </template>
  
  <script>
  import { Client } from '@stomp/stompjs';
  
  export default {
    name: 'websocket',
    props: {
      url: {
        type: String,
        required: true,
      }
    },
    emits: ['connected', 'disconnected', 'message', 'sent', 'error'],
    data() {
      return {
        stompClient: null,
        isConnected: false,
        message: '',
        receivedMessages: []
      };
    },
    methods: {
      connect() {
        console.log(this.url);
        this.stompClient = new Client({
          brokerURL: this.url,
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          onConnect: (iframeConnected) => {
            console.log('Connected to WebSocket');
            this.stompClient.subscribe('/topic/common', message => {
              if (message.body) {
                console.log(`Received: ${message.body}`)
                const parsedMessage = JSON.parse(message.body).content;
                this.receivedMessages.push(parsedMessage);
                this.$emit('message', parsedMessage);  // Emitting 'messageReceived' event
              }
            });
            this.isConnected = true;
            this.$emit('connected');  // Emitting 'connected' event            
          },
          onDisconnect: () => {
            console.log('Disconnected from WebSocket');
            this.isConnected = false;
            this.$emit('disconnected');  // Emitting 'disconnected' event
          },
          onStompError: (iframeError) => {
            console.log('Error on Stomp connection', iframeError.body);
            this.isConnected = false;
            this.$emit('error', iframeError.body);  // Emitting 'error' event
          },
          onWebSocketError: (event) => {
            console.log('Error on WebSocket connection', event);
            this.isConnected = false;
            this.$emit('error', event);  // Emitting 'error' event
          }
        });
        this.stompClient.activate()
      },
      sendMessage() {
        if (this.message.trim()) {
          const body = JSON.stringify({ content: this.message, userId: 1 });
          this.stompClient.publish({destination: "/app/common", body: body});
          this.$emit('sent', this.message);  // Emitting 'messageSent' event
          this.message = '';
        }
      },
      send(body) {
        this.stompClient.publish({destination: "/app/common", body: body});
      },
      disconnect() {
        if (this.stompClient !== null) {
          this.stompClient.deactivate();
          this.$emit('disconnected');  // Emitting 'disconnected' event after deactivation
        }
      }
    },
    beforeDestroy() {
      if (this.isConnected) {
        this.disconnect();
      }
    }
  };
  </script>
  
  <style module>
  .root {
    font-size: 24px;
    width: 600px;
    height: 400px;
  }
  </style>