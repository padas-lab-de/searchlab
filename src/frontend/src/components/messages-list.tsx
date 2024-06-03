import { AssistantMessage, ChatMessage, MessageType } from "@/types";
import { AssistantMessageContent } from "./assistant-message";
import { Separator } from "./ui/separator";
import { UserMessageContent } from "./user-message";
import { AskInput } from "./ask-input";
import { useMessageStore } from "@/stores";

interface MessagesListProps {
  messages: ChatMessage[];
  streamingMessage: AssistantMessage | null;
  onRelatedQuestionSelect: (question: string) => void;
  handleSend: (message: string) => void;
  taskType?: string;
}

const MessagesList = ({
  messages,
  streamingMessage,
  onRelatedQuestionSelect,
  handleSend,
  taskType,
}: MessagesListProps) => {
  const { resetMessages } = useMessageStore(); 

  const handleSendAndReset = (message: string) => {
    handleSend(message);
    if (taskType === "traditional" || taskType === "hybrid") {
      resetMessages(); 
    }
  };

  return (
    <div className="flex flex-col pb-28">
      {messages.map((message, index) =>
        message.role === MessageType.USER ? (
          <>
            {(taskType === "traditional" || taskType === "hybrid") && <AskInput sendMessage={handleSendAndReset} />}
            <UserMessageContent key={index} message={message} />
          </>
        ) : (
          <>
            <AssistantMessageContent
              key={index}
              message={message}
              onRelatedQuestionSelect={onRelatedQuestionSelect}
              taskType={taskType}
            />
            {index !== messages.length - 1 && <Separator />}
          </>
        )
      )}
      {streamingMessage && (
        <AssistantMessageContent
          message={streamingMessage}
          isStreaming={true}
          onRelatedQuestionSelect={onRelatedQuestionSelect}
          taskType={taskType}
        />
      )}
    </div>
  );
};

export default MessagesList;