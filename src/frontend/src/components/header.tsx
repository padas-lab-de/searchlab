import Head from 'next/head';


export type Metadata = {
    metadataBase: URL;
    title: string;
    description: string;
    openGraph: {
        title: string;
        description: string;
    };
};

const Meta = ({ metadata }: { metadata: Metadata }) => {
    return (
        <Head>
            <title>{metadata.title}</title>
            <meta name="description" content={metadata.description} />
            <meta property="og:title" content={metadata.openGraph.title} />
            <meta property="og:description" content={metadata.openGraph.description} />
            <link href="https://fonts.cdnfonts.com/css/google-sans" rel="stylesheet" />
            <link href="https://fonts.cdnfonts.com/css/montserrat" rel="stylesheet" />
        </Head>
    );
};

export default Meta;