export type SearchResult = {
  title: string;
  url: string;
  content: string;
  isRelevant?: boolean;
};

export enum MessageType {
  USER = "user",
  ASSISTANT = "assistant",
}

export type BaseMessage = {
  role: MessageType;
  content: string;
};

export type UserMessage = BaseMessage & {
  role: MessageType.USER;
};

export type AssistantMessage = BaseMessage & {
  role: MessageType.ASSISTANT;
  sources?: SearchResult[];
  relatedQuestions?: string[];
  images?: string[];
};

export type ChatMessage = UserMessage | AssistantMessage;
