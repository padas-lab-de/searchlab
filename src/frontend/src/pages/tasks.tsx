import React, { useState } from 'react';
import { useRouter } from 'next/router';
import { ChatPanel } from "@/components/chat-panel";
import { useMessageStore } from "@/stores";

export default function StartPage() {
  const router = useRouter();
  const { task_id } = router.query;
  const [showInput, setShowInput] = useState(false);
  const [submittedTasks, setSubmittedTasks] = useState<string[]>([]);
  const { resetMessages } = useMessageStore();
  const { messages } = useMessageStore();

  const tasks = [
    {
      id: '1',
      title: 'Swahili Home Cooking',
      description: 'A friend from Kenya is visiting you and you\'d like to surprise him with by cooking a traditional Swahili dish. You want to learn about Swahili dishes and how to cook them. Find web pages about Swahili home cooking.',
      taskType: 'traditional'
    },
    {
      id: '2',
      title: 'Dulles Airport',
      description: 'You want to know what city and state Dulles airport is in; what shuttles, ride-sharing vans, and taxi cabs connect the airport to other cities; what hotels are close to the airport; where cheap off-airport parking is; and where metro stops close to Dulles airport are.',
      taskType: 'conversational'
    },
    {
      id: '3',
      title: 'PhD in Business?',
      description: 'Suppose you have earned a Masters degree in Business. You want to find information to help you decide whether it is worth getting a PhD in Business or not. Find web pages that will help with a cost/benefit analysis of advanced education in Business.',
      taskType: 'hybrid'
    },
  ];

  const currentTask = tasks.find(task => task.id === task_id);

  const handleEndTaskClick = () => {
    setShowInput(!showInput);
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const taskIdAsString = String(task_id);
    const newSubmittedTasks = [...submittedTasks, taskIdAsString];
    setSubmittedTasks(newSubmittedTasks);
    setShowInput(false);
    resetMessages();
    const currentIndex = tasks.findIndex(task => task.id === taskIdAsString);
    const nextTask = tasks[currentIndex + 1];
    if (nextTask) {
      router.push(`/tasks?task_id=${nextTask.id}`);
    } else {
      router.push('/end_session'); 
    }
  };

  return (
    <div style={{ position: 'relative', paddingTop: '60px' }}>
      <div className="flex justify-center gap-4" style={{ position: 'absolute', width: '80%', left: '10%', top: '20px', zIndex: 1000 }}>
        {tasks.map((task, index) => (
          <div
            key={task.id}
            className={`cursor-pointer rounded-full w-8 h-8 flex items-center justify-center ${submittedTasks.includes(task.id) ? 'bg-green-500' : task.id === task_id ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
            onClick={() => {
              if (!submittedTasks.includes(task.id)) {
                router.push(`/tasks?task_id=${task.id}`);
              }
            }}
          >
            {submittedTasks.includes(task.id) ? '✓' : index + 1}
          </div>
        ))}
      </div>
      <div className="text-center pt-10 mt-10 bg-card" style={{ width: '35%', margin: '0 auto', borderRadius: '8px', padding: '10px 30px 20px' }}>
        <h1 className="text-2xl mb-4">{currentTask?.title}</h1>
        <span className="text-l mb-4">{currentTask?.description}</span>
      </div>
      <div className="text-center">
        {!submittedTasks.includes(task_id as string) && (
          <>
            {showInput ? (
              <form onSubmit={handleSubmit} className="text-center" style={{ width: '38%', margin: '0 auto', borderRadius: '8px', padding: '10px 30px 20px' }}>
                <textarea
                  placeholder="Enter your answer"
                  required
                  className="input-answer py-2 px-4 text-lg"
                  style={{ display: 'block', width: 'calc(100% - 8px)', height: '100px', margin: '0 auto', marginBottom: '20px', resize: 'vertical' }} // Adjusted styles for textarea
                ></textarea>
                <button type="submit" className="py-2 px-20 bg-blue-500 text-white rounded-full text-lg">Submit</button>
              </form>
            ) : (
              messages.length > 0 && (
                <button type="submit" className="py-2 px-20 bg-blue-500 text-white rounded-full text-lg mt-2" onClick={handleEndTaskClick}>End Task</button>
              )
            )}
          </>
        )}
      </div>
      <div className="flex grow h-full mx-auto max-w-screen-md px-4 md:px-8">
        <ChatPanel taskType={currentTask?.taskType} />
      </div>
    </div>
  );
}